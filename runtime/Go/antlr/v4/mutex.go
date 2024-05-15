// +build !nomutex

package antlr

import "sync"

type Mutex struct {
	mu sync.Mutex
}

func (m *Mutex) Lock() {
	m.mu.Lock()
}

func (m *Mutex) Unlock() {
	m.mu.Unlock()
}


type RWMutex struct {
	mu sync.RWMutex
}

func (m *RWMutex) Lock() {
	m.mu.Lock()
}

func (m *RWMutex) Unlock() {
	m.mu.Unlock()
}

func (m *RWMutex) RLock() {
	m.mu.RLock()
}

func (m *RWMutex) RUnlock() {
	m.mu.RUnlock()
}