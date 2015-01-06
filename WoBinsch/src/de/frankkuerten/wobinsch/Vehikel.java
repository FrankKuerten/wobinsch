package de.frankkuerten.wobinsch;

public enum Vehikel {

	MB("Motorboot", 30000), SB("Segelboot", 30000), MSB(
			"Segelboot mit Motor", 30000), ZF("Zu Fuß", 30000), A("Auto",
			10000), F("Fahrrad", 20000), M("Motorrad", 10000), B("Bahn", 10000), I(
			"Inliner", 20000), S("Ski", 20000), FZ("Flugzeug", 10000), P(
			"Pferd", 20000);

	private final String bez;
	private final int interval;

	Vehikel(String bez, int interval) {
		this.bez = bez;
		this.interval = interval;
	}

	@Override
	public String toString() {
		return bez;
	}

	public int getInterval() {
		return this.interval;
	}
}
