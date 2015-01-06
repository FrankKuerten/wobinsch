package de.frankkuerten.wobinsch;

import java.util.List;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

public class DBDAOPosition extends SQLiteOpenHelper {

	static String DBNAME = "WOBINSCH";
	static String T_POSITION = "T_POSITION";
	static String COL_ID = "_ID";
	static String COL_LAT = "LATITUDE";
	static String COL_LON = "LONGITUDE";
	static String COL_VEL = "VELOCITY";
	static String COL_ACC = "ACCURACY";
	static String COL_VEH = "VEHICLE";

	public DBDAOPosition(Context context) {
		super(context, DBNAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer create = new StringBuffer();
		create.append("CREATE TABLE ").append(T_POSITION);
		create.append(" (");
		create.append(COL_ID).append(" INTEGER PRIMARY KEY , ");
		create.append(COL_LAT).append(" REAL , ");
		create.append(COL_LON).append(" REAL , ");
		create.append(COL_VEL).append(" REAL , ");
		create.append(COL_ACC).append(" REAL , ");
		create.append(COL_VEH).append(" TEXT ");
		create.append(")");
		db.execSQL(create.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		StringBuffer drop = new StringBuffer();
		drop.append("DROP TABLE ").append(T_POSITION);
		db.execSQL(drop.toString());
		onCreate(db);
	}

	public void insert(Position pos) {
		if (pos == null || pos.getOrt() == null) {
			return;
		}
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(COL_ID, pos.getOrt().getTime());
		cv.put(COL_LAT, pos.getOrt().getLatitude());
		cv.put(COL_LON, pos.getOrt().getLongitude());
		cv.put(COL_VEL, pos.getOrt().getSpeed());
		cv.put(COL_ACC, pos.getOrt().getAccuracy());
		cv.put(COL_VEH, pos.getVehikel().name());
		db.insert(T_POSITION, COL_ID, cv);
		db.close();
	}

	public Cursor readAll() {
		StringBuffer sel = new StringBuffer();
		sel.append("SELECT * FROM ").append(T_POSITION).append(" ORDER BY ")
				.append(COL_ID);
		return getReadableDatabase().rawQuery(sel.toString(), new String[] {});
	}

	public List<TeilStrecke> initFromDB() {
		Cursor c = readAll();
		List<TeilStrecke> erg = new Vector<TeilStrecke>();
		Position pos = null;
		Position posVorher = null;
		TeilStrecke ts = null;
		float[] entfernung = {0};
		if (c.getCount() > 0) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				pos = new Position();
				pos.setVehikel(Vehikel.valueOf(c.getString(c
						.getColumnIndex(COL_VEH))));
				Location ort = pos.getOrt();
				ort.setTime(c.getLong(c.getColumnIndex(COL_ID)));
				ort.setLongitude(c.getFloat(c.getColumnIndex(COL_LON)));
				ort.setLatitude(c.getFloat(c.getColumnIndex(COL_LAT)));
				ort.setSpeed(c.getFloat(c.getColumnIndex(COL_VEL)));
				ort.setAccuracy(c.getFloat(c.getColumnIndex(COL_ACC)));

				if (posVorher == null
				// oder Vehikel gewechselt
						|| pos.getVehikel() != posVorher.getVehikel()
						// oder 1 Minute Unterbrechung
						|| pos.getOrt().getTime()
								- posVorher.getOrt().getTime() > 60000) {
					ts = new TeilStrecke();
					erg.add(ts);
				} else {
					Location.distanceBetween(posVorher.getLat(),
							posVorher.getLon(), pos.getLat(), pos.getLon(),
							entfernung);
					if (entfernung.length > 0){
						ts.setGesamtLaenge(ts.getGesamtLaenge() + entfernung[0]);
					}
				}
				if (ts.getGesamtLaenge() > 0){
					long anfang = ts.getPositionen().get(0).getOrt().getTime();
					long ende = ts.getPositionen().get(ts.getPositionen().size() - 1).getOrt().getTime();
					ts.setSchnittGeschwindigkeit(ts.getGesamtLaenge() / ((ende - anfang) / 1000));
				}

				ts.addPosition(pos);
				posVorher = pos;
				c.moveToNext();
			}
		}
		c.close();
		return erg;
	}
}
