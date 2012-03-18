public class EmptyPredictionContext extends SingletonPredictionContext {
	public EmptyPredictionContext() {
		super(null,"$");
	}

	@Override
	public int findPayload(String payload) {
		return 1;
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public PredictionContext getParent(int index) {
		return null;
	}

	@Override
	public String getPayload(int index) {
		return payload;
	}
}
