//--------------------------------------------------------------------------
// 
//  Copyright (c) Microsoft Corporation.  All rights reserved. 
// 
//  File: QueuedTaskScheduler.cs
//
//--------------------------------------------------------------------------

using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;

namespace System.Threading.Tasks.Schedulers
{
    /// <summary>
    /// Provides a TaskScheduler that provides control over priorities, fairness, and the underlying threads utilized.
    /// </summary>
    [DebuggerTypeProxy(typeof(QueuedTaskSchedulerDebugView))]
    [DebuggerDisplay("Id={Id}, Queues={DebugQueueCount}, ScheduledTasks = {DebugTaskCount}")]
    public sealed class QueuedTaskScheduler : TaskScheduler, IDisposable
    {
        /// <summary>Debug view for the QueuedTaskScheduler.</summary>
        private class QueuedTaskSchedulerDebugView
        {
            /// <summary>The scheduler.</summary>
            private QueuedTaskScheduler _scheduler;

            /// <summary>Initializes the debug view.</summary>
            /// <param name="scheduler">The scheduler.</param>
            public QueuedTaskSchedulerDebugView(QueuedTaskScheduler scheduler)
            {
                if (scheduler == null) throw new ArgumentNullException("scheduler");
                _scheduler = scheduler;
            }

            /// <summary>Gets all of the Tasks queued to the scheduler directly.</summary>
            public IEnumerable<Task> ScheduledTasks
            {
                get
                {
                    var tasks = (_scheduler._targetScheduler != null) ?
                        (IEnumerable<Task>)_scheduler._nonthreadsafeTaskQueue :
                        (IEnumerable<Task>)_scheduler._blockingTaskQueue;
                    return tasks.Where(t => t != null).ToList();
                }
            }

            /// <summary>Gets the prioritized and fair queues.</summary>
            public IEnumerable<TaskScheduler> Queues
            {
                get
                {
                    List<TaskScheduler> queues = new List<TaskScheduler>();
                    foreach (var group in _scheduler._queueGroups) queues.AddRange(group.Value);
                    return queues;
                }
            }
        }

        /// <summary>
        /// A sorted list of round-robin queue lists.  Tasks with the smallest priority value
        /// are preferred.  Priority groups are round-robin'd through in order of priority.
        /// </summary>
        private readonly SortedList<int, QueueGroup> _queueGroups = new SortedList<int, QueueGroup>();
        /// <summary>Cancellation token used for disposal.</summary>
        private readonly CancellationTokenSource _disposeCancellation = new CancellationTokenSource();
        /// <summary>
        /// The maximum allowed concurrency level of this scheduler.  If custom threads are
        /// used, this represents the number of created threads.
        /// </summary>
        private readonly int _concurrencyLevel;
        /// <summary>Whether we're processing tasks on the current thread.</summary>
        private static ThreadLocal<bool> _taskProcessingThread = new ThreadLocal<bool>();

        // ***
        // *** For when using a target scheduler
        // ***

        /// <summary>The scheduler onto which actual work is scheduled.</summary>
        private readonly TaskScheduler _targetScheduler;
        /// <summary>The queue of tasks to process when using an underlying target scheduler.</summary>
        private readonly Queue<Task> _nonthreadsafeTaskQueue;
        /// <summary>The number of Tasks that have been queued or that are running whiel using an underlying scheduler.</summary>
        private int _delegatesQueuedOrRunning = 0;

        // ***
        // *** For when using our own threads
        // ***

        /// <summary>The threads used by the scheduler to process work.</summary>
        private readonly Thread[] _threads;
        /// <summary>The collection of tasks to be executed on our custom threads.</summary>
        private readonly BlockingCollection<Task> _blockingTaskQueue;

        // ***

        /// <summary>Initializes the scheduler.</summary>
        public QueuedTaskScheduler() : this(TaskScheduler.Default, 0) { }

        /// <summary>Initializes the scheduler.</summary>
        /// <param name="targetScheduler">The target underlying scheduler onto which this sceduler's work is queued.</param>
        public QueuedTaskScheduler(TaskScheduler targetScheduler) : this(targetScheduler, 0) { }

        /// <summary>Initializes the scheduler.</summary>
        /// <param name="targetScheduler">The target underlying scheduler onto which this sceduler's work is queued.</param>
        /// <param name="maxConcurrencyLevel">The maximum degree of concurrency allowed for this scheduler's work.</param>
        public QueuedTaskScheduler(
            TaskScheduler targetScheduler,
            int maxConcurrencyLevel)
        {
            // Validate arguments
            if (targetScheduler == null) throw new ArgumentNullException("underlyingScheduler");
            if (maxConcurrencyLevel < 0) throw new ArgumentOutOfRangeException("concurrencyLevel");

            // Initialize only those fields relevant to use an underlying scheduler.  We don't
            // initialize the fields relevant to using our own custom threads.
            _targetScheduler = targetScheduler;
            _nonthreadsafeTaskQueue = new Queue<Task>();

            // If 0, use the number of logical processors.  But make sure whatever value we pick
            // is not greater than the degree of parallelism allowed by the underlying scheduler.
            _concurrencyLevel = maxConcurrencyLevel != 0 ? maxConcurrencyLevel : Environment.ProcessorCount;
            if (targetScheduler.MaximumConcurrencyLevel > 0 && 
                targetScheduler.MaximumConcurrencyLevel < _concurrencyLevel)
            {
                _concurrencyLevel = targetScheduler.MaximumConcurrencyLevel;
            }
        }

        /// <summary>Initializes the scheduler.</summary>
        /// <param name="threadCount">The number of threads to create and use for processing work items.</param>
        public QueuedTaskScheduler(int threadCount) : this(threadCount, string.Empty, false, ThreadPriority.Normal, ApartmentState.MTA, 0, null, null) { }

        /// <summary>Initializes the scheduler.</summary>
        /// <param name="threadCount">The number of threads to create and use for processing work items.</param>
        /// <param name="threadName">The name to use for each of the created threads.</param>
        /// <param name="useForegroundThreads">A Boolean value that indicates whether to use foreground threads instead of background.</param>
        /// <param name="threadPriority">The priority to assign to each thread.</param>
        /// <param name="threadApartmentState">The apartment state to use for each thread.</param>
        /// <param name="threadMaxStackSize">The stack size to use for each thread.</param>
        /// <param name="threadInit">An initialization routine to run on each thread.</param>
        /// <param name="threadFinally">A finalization routine to run on each thread.</param>
        public QueuedTaskScheduler(
            int threadCount,
            string threadName = "",
            bool useForegroundThreads = false,
            ThreadPriority threadPriority = ThreadPriority.Normal,
            ApartmentState threadApartmentState = ApartmentState.MTA,
            int threadMaxStackSize = 0,
            Action threadInit = null,
            Action threadFinally = null)
        {
            // Validates arguments (some validation is left up to the Thread type itself).
            // If the thread count is 0, default to the number of logical processors.
            if (threadCount < 0) throw new ArgumentOutOfRangeException("concurrencyLevel");
            else if (threadCount == 0) _concurrencyLevel = Environment.ProcessorCount;
            else _concurrencyLevel = threadCount;

            // Initialize the queue used for storing tasks
            _blockingTaskQueue = new BlockingCollection<Task>();

            // Create all of the threads
            _threads = new Thread[threadCount];
            for (int i = 0; i < threadCount; i++)
            {
                _threads[i] = new Thread(() => ThreadBasedDispatchLoop(threadInit, threadFinally), threadMaxStackSize)
                {
                    Priority = threadPriority,
                    IsBackground = !useForegroundThreads,
                };
                if (threadName != null) _threads[i].Name = threadName + " (" + i + ")";
                _threads[i].SetApartmentState(threadApartmentState);
            }

            // Start all of the threads
            foreach (var thread in _threads) thread.Start();
        }

        /// <summary>The dispatch loop run by all threads in this scheduler.</summary>
        /// <param name="threadInit">An initialization routine to run when the thread begins.</param>
        /// <param name="threadFinally">A finalization routine to run before the thread ends.</param>
        private void ThreadBasedDispatchLoop(Action threadInit, Action threadFinally)
        {
            _taskProcessingThread.Value = true;
            if (threadInit != null) threadInit();
            try
            {
                // If the scheduler is disposed, the cancellation token will be set and
                // we'll receive an OperationCanceledException.  That OCE should not crash the process.
                try
                {
                    // If a thread abort occurs, we'll try to reset it and continue running.
                    while (true)
                    {
                        try
                        {
                            // For each task queued to the scheduler, try to execute it.
                            foreach (var task in _blockingTaskQueue.GetConsumingEnumerable(_disposeCancellation.Token))
                            {
                                // If the task is not null, that means it was queued to this scheduler directly.
                                // Run it.
                                if (task != null)
                                {
                                    TryExecuteTask(task);
                                }
                                // If the task is null, that means it's just a placeholder for a task
                                // queued to one of the subschedulers.  Find the next task based on
                                // priority and fairness and run it.
                                else
                                {
                                    // Find the next task based on our ordering rules...
                                    Task targetTask;
                                    QueuedTaskSchedulerQueue queueForTargetTask;
                                    lock (_queueGroups) FindNextTask_NeedsLock(out targetTask, out queueForTargetTask);

                                    // ... and if we found one, run it
                                    if (targetTask != null) queueForTargetTask.ExecuteTask(targetTask);
                                }
                            }
                        }
                        catch (ThreadAbortException)
                        {
                            // If we received a thread abort, and that thread abort was due to shutting down
                            // or unloading, let it pass through.  Otherwise, reset the abort so we can
                            // continue processing work items.
                            if (!Environment.HasShutdownStarted && !AppDomain.CurrentDomain.IsFinalizingForUnload())
                            {
                                Thread.ResetAbort();
                            }
                        }
                    }
                }
                catch (OperationCanceledException) { }
            }
            finally
            {
                // Run a cleanup routine if there was one
                if (threadFinally != null) threadFinally();
                _taskProcessingThread.Value = false;
            }
        }

        /// <summary>Gets the number of queues currently activated.</summary>
        private int DebugQueueCount
        {
            get
            {
                int count = 0;
                foreach (var group in _queueGroups) count += group.Value.Count;
                return count;
            }
        }

        /// <summary>Gets the number of tasks currently scheduled.</summary>
        private int DebugTaskCount
        {
            get
            {
                return (_targetScheduler != null ? 
                    (IEnumerable<Task>)_nonthreadsafeTaskQueue : (IEnumerable<Task>)_blockingTaskQueue)
                    .Where(t => t != null).Count();
            }
        }

        /// <summary>Find the next task that should be executed, based on priorities and fairness and the like.</summary>
        /// <param name="targetTask">The found task, or null if none was found.</param>
        /// <param name="queueForTargetTask">
        /// The scheduler associated with the found task.  Due to security checks inside of TPL,  
        /// this scheduler needs to be used to execute that task.
        /// </param>
        private void FindNextTask_NeedsLock(out Task targetTask, out QueuedTaskSchedulerQueue queueForTargetTask)
        {
            targetTask = null;
            queueForTargetTask = null;

            // Look through each of our queue groups in sorted order.
            // This ordering is based on the priority of the queues.
            foreach (var queueGroup in _queueGroups)
            {
                var queues = queueGroup.Value;

                // Within each group, iterate through the queues in a round-robin
                // fashion.  Every time we iterate again and successfully find a task, 
                // we'll start in the next location in the group.
                foreach (int i in queues.CreateSearchOrder())
                {
                    queueForTargetTask = queues[i];
                    var items = queueForTargetTask._workItems;
                    if (items.Count > 0)
                    {
                        targetTask = items.Dequeue();
                        if (queueForTargetTask._disposed && items.Count == 0)
                        {
                            RemoveQueue_NeedsLock(queueForTargetTask);
                        }
                        queues.NextQueueIndex = (queues.NextQueueIndex + 1) % queueGroup.Value.Count;
                        return;
                    }
                }
            }
        }

        /// <summary>Queues a task to the scheduler.</summary>
        /// <param name="task">The task to be queued.</param>
        protected override void QueueTask(Task task)
        {
            // If we've been disposed, no one should be queueing
            if (_disposeCancellation.IsCancellationRequested) throw new ObjectDisposedException(GetType().Name);

            // If the target scheduler is null (meaning we're using our own threads),
            // add the task to the blocking queue
            if (_targetScheduler == null)
            {
                _blockingTaskQueue.Add(task);
            }
            // Otherwise, add the task to the non-blocking queue,
            // and if there isn't already an executing processing task,
            // start one up
            else
            {
                // Queue the task and check whether we should launch a processing
                // task (noting it if we do, so that other threads don't result
                // in queueing up too many).
                bool launchTask = false;
                lock (_nonthreadsafeTaskQueue)
                {
                    _nonthreadsafeTaskQueue.Enqueue(task);
                    if (_delegatesQueuedOrRunning < _concurrencyLevel)
                    {
                        ++_delegatesQueuedOrRunning;
                        launchTask = true;
                    }
                }

                // If necessary, start processing asynchronously
                if (launchTask)
                {
                    Task.Factory.StartNew(ProcessPrioritizedAndBatchedTasks,
                        CancellationToken.None, TaskCreationOptions.None, _targetScheduler);
                }
            }
        }

        /// <summary>
        /// Process tasks one at a time in the best order.  
        /// This should be run in a Task generated by QueueTask.
        /// It's been separated out into its own method to show up better in Parallel Tasks.
        /// </summary>
        private void ProcessPrioritizedAndBatchedTasks()
        {
            bool continueProcessing = true;
            while (!_disposeCancellation.IsCancellationRequested && continueProcessing)
            {
                try
                {
                    // Note that we're processing tasks on this thread
                    _taskProcessingThread.Value = true;

                    // Until there are no more tasks to process
                    while (!_disposeCancellation.IsCancellationRequested)
                    {
                        // Try to get the next task.  If there aren't any more, we're done.
                        Task targetTask;
                        lock (_nonthreadsafeTaskQueue)
                        {
                            if (_nonthreadsafeTaskQueue.Count == 0) return;
                            targetTask = _nonthreadsafeTaskQueue.Dequeue();
                        }

                        // If the task is null, it's a placeholder for a task in the round-robin queues.
                        // Find the next one that should be processed.
                        QueuedTaskSchedulerQueue queueForTargetTask = null;
                        if (targetTask == null)
                        {
                            lock (_queueGroups) FindNextTask_NeedsLock(out targetTask, out queueForTargetTask);
                        }

                        // Now if we finally have a task, run it.  If the task
                        // was associated with one of the round-robin schedulers, we need to use it
                        // as a thunk to execute its task.
                        if (targetTask != null)
                        {
                            if (queueForTargetTask != null) queueForTargetTask.ExecuteTask(targetTask);
                            else TryExecuteTask(targetTask);
                        }
                    }
                }
                finally
                {
                    // Now that we think we're done, verify that there really is
                    // no more work to do.  If there's not, highlight
                    // that we're now less parallel than we were a moment ago.
                    lock (_nonthreadsafeTaskQueue)
                    {
                        if (_nonthreadsafeTaskQueue.Count == 0)
                        {
                            _delegatesQueuedOrRunning--;
                            continueProcessing = false;
                            _taskProcessingThread.Value = false;
                        }
                    }
                }
            }
        }

        /// <summary>Notifies the pool that there's a new item to be executed in one of the round-robin queues.</summary>
        private void NotifyNewWorkItem() { QueueTask(null); }

        /// <summary>Tries to execute a task synchronously on the current thread.</summary>
        /// <param name="task">The task to execute.</param>
        /// <param name="taskWasPreviouslyQueued">Whether the task was previously queued.</param>
        /// <returns>true if the task was executed; otherwise, false.</returns>
        protected override bool TryExecuteTaskInline(Task task, bool taskWasPreviouslyQueued)
        {
            // If we're already running tasks on this threads, enable inlining
            return _taskProcessingThread.Value && TryExecuteTask(task);
        }

        /// <summary>Gets the tasks scheduled to this scheduler.</summary>
        /// <returns>An enumerable of all tasks queued to this scheduler.</returns>
        /// <remarks>This does not include the tasks on sub-schedulers.  Those will be retrieved by the debugger separately.</remarks>
        protected override IEnumerable<Task> GetScheduledTasks()
        {
            // If we're running on our own threads, get the tasks from the blocking queue...
            if (_targetScheduler == null)
            {
                // Get all of the tasks, filtering out nulls, which are just placeholders
                // for tasks in other sub-schedulers
                return _blockingTaskQueue.Where(t => t != null).ToList();
            }
            // otherwise get them from the non-blocking queue...
            else
            {
                return _nonthreadsafeTaskQueue.Where(t => t != null).ToList();
            }
        }

        /// <summary>Gets the maximum concurrency level to use when processing tasks.</summary>
        public override int MaximumConcurrencyLevel { get { return _concurrencyLevel; } }

        /// <summary>Initiates shutdown of the scheduler.</summary>
        public void Dispose()
        {
            _disposeCancellation.Cancel();
        }

        /// <summary>Creates and activates a new scheduling queue for this scheduler.</summary>
        /// <returns>The newly created and activated queue at priority 0.</returns>
        public TaskScheduler ActivateNewQueue() { return ActivateNewQueue(0); }

        /// <summary>Creates and activates a new scheduling queue for this scheduler.</summary>
        /// <param name="priority">The priority level for the new queue.</param>
        /// <returns>The newly created and activated queue at the specified priority.</returns>
        public TaskScheduler ActivateNewQueue(int priority)
        {
            // Create the queue
            var createdQueue = new QueuedTaskSchedulerQueue(priority, this);

            // Add the queue to the appropriate queue group based on priority
            lock (_queueGroups)
            {
                QueueGroup list;
                if (!_queueGroups.TryGetValue(priority, out list))
                {
                    list = new QueueGroup();
                    _queueGroups.Add(priority, list);
                }
                list.Add(createdQueue);
            }

            // Hand the new queue back
            return createdQueue;
        }

        /// <summary>Removes a scheduler from the group.</summary>
        /// <param name="queue">The scheduler to be removed.</param>
        private void RemoveQueue_NeedsLock(QueuedTaskSchedulerQueue queue)
        {
            // Find the group that contains the queue and the queue's index within the group
            var queueGroup = _queueGroups[queue._priority];
            int index = queueGroup.IndexOf(queue);

            // We're about to remove the queue, so adjust the index of the next
            // round-robin starting location if it'll be affected by the removal
            if (queueGroup.NextQueueIndex >= index) queueGroup.NextQueueIndex--;

            // Remove it
            queueGroup.RemoveAt(index);
        }

        /// <summary>A group of queues a the same priority level.</summary>
        private class QueueGroup : List<QueuedTaskSchedulerQueue>
        {
            /// <summary>The starting index for the next round-robin traversal.</summary>
            public int NextQueueIndex = 0;

            /// <summary>Creates a search order through this group.</summary>
            /// <returns>An enumerable of indices for this group.</returns>
            public IEnumerable<int> CreateSearchOrder()
            {
                for (int i = NextQueueIndex; i < Count; i++) yield return i;
                for (int i = 0; i < NextQueueIndex; i++) yield return i;
            }
        }

        /// <summary>Provides a scheduling queue associatd with a QueuedTaskScheduler.</summary>
        [DebuggerDisplay("QueuePriority = {_priority}, WaitingTasks = {WaitingTasks}")]
        [DebuggerTypeProxy(typeof(QueuedTaskSchedulerQueueDebugView))]
        private sealed class QueuedTaskSchedulerQueue : TaskScheduler, IDisposable
        {
            /// <summary>A debug view for the queue.</summary>
            private sealed class QueuedTaskSchedulerQueueDebugView
            {
                /// <summary>The queue.</summary>
                private readonly QueuedTaskSchedulerQueue _queue;

                /// <summary>Initializes the debug view.</summary>
                /// <param name="queue">The queue to be debugged.</param>
                public QueuedTaskSchedulerQueueDebugView(QueuedTaskSchedulerQueue queue)
                {
                    if (queue == null) throw new ArgumentNullException("queue");
                    _queue = queue;
                }

                /// <summary>Gets the priority of this queue in its associated scheduler.</summary>
                public int Priority { get { return _queue._priority; } }
                /// <summary>Gets the ID of this scheduler.</summary>
                public int Id { get { return _queue.Id; } }
                /// <summary>Gets all of the tasks scheduled to this queue.</summary>
                public IEnumerable<Task> ScheduledTasks { get { return _queue.GetScheduledTasks(); } }
                /// <summary>Gets the QueuedTaskScheduler with which this queue is associated.</summary>
                public QueuedTaskScheduler AssociatedScheduler { get { return _queue._pool; } }
            }

            /// <summary>The scheduler with which this pool is associated.</summary>
            private readonly QueuedTaskScheduler _pool;
            /// <summary>The work items stored in this queue.</summary>
            internal readonly Queue<Task> _workItems;
            /// <summary>Whether this queue has been disposed.</summary>
            internal bool _disposed;
            /// <summary>Gets the priority for this queue.</summary>
            internal int _priority;

            /// <summary>Initializes the queue.</summary>
            /// <param name="priority">The priority associated with this queue.</param>
            /// <param name="pool">The scheduler with which this queue is associated.</param>
            internal QueuedTaskSchedulerQueue(int priority, QueuedTaskScheduler pool)
            {
                _priority = priority;
                _pool = pool;
                _workItems = new Queue<Task>();
            }

            /// <summary>Gets the number of tasks waiting in this scheduler.</summary>
            internal int WaitingTasks { get { return _workItems.Count; } }

            /// <summary>Gets the tasks scheduled to this scheduler.</summary>
            /// <returns>An enumerable of all tasks queued to this scheduler.</returns>
            protected override IEnumerable<Task> GetScheduledTasks() { return _workItems.ToList(); }

            /// <summary>Queues a task to the scheduler.</summary>
            /// <param name="task">The task to be queued.</param>
            protected override void QueueTask(Task task)
            {
                if (_disposed) throw new ObjectDisposedException(GetType().Name);

                // Queue up the task locally to this queue, and then notify
                // the parent scheduler that there's work available
                lock (_pool._queueGroups) _workItems.Enqueue(task);
                _pool.NotifyNewWorkItem();
            }

            /// <summary>Tries to execute a task synchronously on the current thread.</summary>
            /// <param name="task">The task to execute.</param>
            /// <param name="taskWasPreviouslyQueued">Whether the task was previously queued.</param>
            /// <returns>true if the task was executed; otherwise, false.</returns>
            protected override bool TryExecuteTaskInline(Task task, bool taskWasPreviouslyQueued)
            {
                // If we're using our own threads and if this is being called from one of them,
                // or if we're currently processing another task on this thread, try running it inline.
                return _taskProcessingThread.Value && TryExecuteTask(task);
            }

            /// <summary>Runs the specified ask.</summary>
            /// <param name="task">The task to execute.</param>
            internal void ExecuteTask(Task task) { TryExecuteTask(task); }

            /// <summary>Gets the maximum concurrency level to use when processing tasks.</summary>
            public override int MaximumConcurrencyLevel { get { return _pool.MaximumConcurrencyLevel; } }

            /// <summary>Signals that the queue should be removed from the scheduler as soon as the queue is empty.</summary>
            public void Dispose()
            {
                if (!_disposed)
                {
                    lock (_pool._queueGroups)
                    {
                        // We only remove the queue if it's empty.  If it's not empty,
                        // we still mark it as disposed, and the associated QueuedTaskScheduler
                        // will remove the queue when its count hits 0 and its _disposed is true.
                        if (_workItems.Count == 0)
                        {
                            _pool.RemoveQueue_NeedsLock(this);
                        }
                    }
                    _disposed = true;
                }
            }
        }
    }
}