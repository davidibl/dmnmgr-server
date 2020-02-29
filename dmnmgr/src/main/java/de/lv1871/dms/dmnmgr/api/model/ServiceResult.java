package de.lv1871.dms.dmnmgr.api.model;

public class ServiceResult {

	private boolean success;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public static class ServiceResultBuilder {
		private boolean success;

		public static ServiceResultBuilder create() {
			return new ServiceResultBuilder();
		}

		public ServiceResultBuilder withResult(boolean success) {
			this.success = success;
			return this;
		}

		public ServiceResult build() {
			ServiceResult result = new ServiceResult();
			result.setSuccess(success);
			return result;
		}
	}
}
