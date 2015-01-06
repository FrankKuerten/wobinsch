package de.frankkuerten.wobinsch;

public enum Vehikel {

	MB("Motorboot"), SB("Segelboot"), MSB("Segelboot mit Motor"), ZF("Zu Fuß"), A(
			"Auto"), F("Fahrrad"), M("Motorrad"), B("Bahn"), I("Inliner"), S(
			"Ski"), FZ("Flugzeug"), P("Pferd");

	private final String bez;

	Vehikel(String bez) {
		this.bez = bez;
	}

	@Override
	public String toString() {
		return bez;
	}
}
