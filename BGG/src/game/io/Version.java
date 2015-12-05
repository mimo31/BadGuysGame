package game.io;

class Version {

	public enum ReleaseType {
		ALPHA(0), BETA(1), FINAL(2);

		private ReleaseType(int degree) {
			this.degree = degree;
		}

		private int degree;
	}

	public int major;
	public int minor;
	public int build;
	public ReleaseType type;

	public Version() {
		this.major = 1;
		this.minor = 2;
		this.build = 0;
		this.type = ReleaseType.BETA;
	}

	public boolean isNewer(Version anotherVersion) {
		if (this.major > anotherVersion.major) {
			return true;
		}
		else if (this.major < anotherVersion.major) {
			return false;
		}
		else {
			if (this.minor > anotherVersion.minor) {
				return true;
			}
			else if (this.minor < anotherVersion.minor) {
				return false;
			}
			else {
				if (this.type == anotherVersion.type) {
					if (this.build > anotherVersion.build) {
						return true;
					}
					else {
						return false;
					}
				}
				else if (isTypeNewer(this.type, anotherVersion.type)) {
					return true;
				}
				else {
					return false;
				}
			}
		}
	}

	private static boolean isTypeNewer(ReleaseType type1, ReleaseType type2) {
		return type1.degree > type2.degree;
	}

	public Version(String versionString) throws VersionStringFormatException {
		if (checkVersionString(versionString)) {
			versionString = versionString.toLowerCase();
			if (versionString.contains("alpha")) {
				this.type = ReleaseType.ALPHA;
				versionString = versionString.replaceFirst("alpha", "");
			}
			else if (versionString.contains("beta")) {
				this.type = ReleaseType.BETA;
				versionString = versionString.replaceFirst("beta", "");
			}
			else if (versionString.contains("final")) {
				this.type = ReleaseType.FINAL;
				versionString.replaceFirst("final", "");
			}
			else {
				this.type = ReleaseType.FINAL;
			}
			versionString = new String(versionString);
			String[] knownVersionValues = versionString.split("\\.");
			int[] knownValues = new int[knownVersionValues.length];
			for (int i = 0; i < knownValues.length; i++) {
				knownValues[i] = Integer.parseInt(knownVersionValues[i]);
			}
			this.major = knownValues[0];
			if (knownValues.length >= 2) {
				this.minor = knownValues[1];
				if (knownValues.length == 3) {
					this.build = knownValues[2];
				}
				else {
					this.build = 0;
				}
			}
			else {
				this.minor = 0;
				this.build = 0;
			}

		}
		else {
			throw new VersionStringFormatException();
		}
	}

	@Override
	public String toString() {
		return String.valueOf(this.major) + "." + String.valueOf(this.minor) + "." + String.valueOf(this.build) + this.typeToString();
	}

	private String typeToString() {
		switch (this.type) {
			case ALPHA:
				return "alpha";
			case BETA:
				return "beta";
			case FINAL:
				return "final";
		}
		return null;
	}

	@SuppressWarnings("serial")
	public static class VersionStringFormatException extends Exception {

	}

	private static boolean checkVersionString(String s) {
		s = s.toLowerCase();
		if (s.contains("alpha")) {
			s = s.replaceFirst("alpha", "");
		}
		else if (s.contains("beta")) {
			s = s.replaceFirst("beta", "");
		}
		else if (s.contains("final")) {
			s = s.replaceFirst("final", "");
		}
		char[] numberDotArray = s.toCharArray();
		boolean lastWasDot = true;
		int numberOfDots = 0;
		for (int i = 0; i < numberDotArray.length; i++) {
			if (!Character.isDigit(numberDotArray[i]) && numberDotArray[i] != '.') {
				return false;
			}
			else {
				if (numberDotArray[i] == '.') {
					if (lastWasDot) {
						return false;
					}
					else {
						lastWasDot = true;
					}
					numberOfDots++;
					if (numberOfDots > 2) {
						return false;
					}
				}
				else {
					lastWasDot = false;
				}
			}
		}
		if (lastWasDot) {
			return false;
		}
		else {
			return true;
		}
	}
}
