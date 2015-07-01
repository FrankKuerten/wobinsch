package de.frankkuerten.wobinsch;

public enum Einheiten {
	M("Meter", "m", "m/s", 1, 1), KM("Kilometer", "km", "km/h", 1000, 3600), LM(
			"Meilen", "mi", "mph", 1609.344F, 3600), SM("Seemeilen", "sm", "kn",
			1852, 3600);

	private final String bez;
	private final String labelLaenge;
	private final String labelGeschw;
	private final float konvLaenge;
	private final int konvZeit;

	private Einheiten(String bez, String labelLaenge, String labelGeschw,
			float konvLaenge, int konvZeit) {
		this.bez = bez;
		this.labelLaenge = labelLaenge;
		this.labelGeschw = labelGeschw;
		this.konvLaenge = konvLaenge;
		this.konvZeit = konvZeit;
	}

	@Override
	public String toString() {
		return bez;
	}

	public float getKonvLaenge() {
		return konvLaenge;
	}

	public int getKonvZeit() {
		return konvZeit;
	}

	public String getLabelLaenge() {
		return labelLaenge;
	}

	public String getLabelGeschw() {
		return labelGeschw;
	}

}
