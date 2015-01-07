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

	private static DBDAOPosition instance;

	static String DBNAME = "WOBINSCH";
	static String T_POSITION = "T_POSITION";
	static String COL_ID = "_ID";
	static String COL_LAT = "LATITUDE";
	static String COL_LON = "LONGITUDE";
	static String COL_VEL = "VELOCITY";
	static String COL_ACC = "ACCURACY";
	static String COL_VEH = "VEHICLE";

	private DBDAOPosition(Context context) {
		super(context, DBNAME, null, 1);
	}

	public static DBDAOPosition instance(Context context) {
		if (instance == null) {
			instance = new DBDAOPosition(context);
		}
		return instance;
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

	public void delete(Position pos) {
		if (pos == null || pos.getOrt() == null) {
			return;
		}
		SQLiteDatabase db = this.getWritableDatabase();
		StringBuffer del = new StringBuffer();
		del.append("DELETE FROM ").append(T_POSITION).append(" WHERE ")
				.append(COL_ID).append(" = ").append(pos.getOrt().getTime());

		db.execSQL(del.toString());
		db.close();
	}

	private Cursor readAll() {
		StringBuffer sel = new StringBuffer();
		sel.append("SELECT * FROM ").append(T_POSITION).append(" ORDER BY ")
				.append(COL_ID);
		return getReadableDatabase().rawQuery(sel.toString(), new String[] {});
	}

	public List<Position> readAllPositions() {
		Cursor c = readAll();
		List<Position> erg = new Vector<Position>();
		Position pos = null;
		if (c.getCount() > 0) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				pos = new Position();
				this.mapColumns(c, pos);
				erg.add(pos);
				c.moveToNext();
			}
		}
		c.close();
		return erg;
	}

	private Cursor readAllReverse() {
		StringBuffer sel = new StringBuffer();
		sel.append("SELECT * FROM ").append(T_POSITION).append(" ORDER BY ")
				.append(COL_ID).append(" DESC");
		return getReadableDatabase().rawQuery(sel.toString(), new String[] {});
	}
	
	public List<Position> readNewestBlock() {
		Cursor c = readAllReverse();
		List<Position> erg = new Vector<Position>();
		Position pos = null;
		Position posVorher = null;
		if (c.getCount() > 0) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				pos = new Position();
				this.mapColumns(c, pos);
				if (posVorher != null 
						// Wechsel der TeilStrecke?
						&& (pos.getVehikel() != posVorher.getVehikel()
						|| posVorher.getOrt().getTime() - pos.getOrt().getTime() > 600000)){
					break;
				}
				erg.add(0, pos);
				posVorher = pos;
				c.moveToNext();
			}
		}
		c.close();
		return erg;
	}

	private void mapColumns(Cursor c, Position pos) {
		pos.setVehikel(Vehikel.valueOf(c.getString(c.getColumnIndex(COL_VEH))));
		Location ort = pos.getOrt();
		ort.setTime(c.getLong(c.getColumnIndex(COL_ID)));
		ort.setLongitude(c.getFloat(c.getColumnIndex(COL_LON)));
		ort.setLatitude(c.getFloat(c.getColumnIndex(COL_LAT)));
		ort.setSpeed(c.getFloat(c.getColumnIndex(COL_VEL)));
		ort.setAccuracy(c.getFloat(c.getColumnIndex(COL_ACC)));
	}
}
