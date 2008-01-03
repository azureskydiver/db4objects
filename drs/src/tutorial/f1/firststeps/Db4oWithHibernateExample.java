package f1.firststeps;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.hibernate.HibernateReplication;

import org.hibernate.cfg.Configuration;

import java.io.File;

public class Db4oWithHibernateExample {
	public static void main(String[] args) {
		Pilot pilot1 = new Pilot("Scott Felton", 200);
		Pilot pilot2 = new Pilot("Frank Green", 120);

		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
		Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);

		ObjectContainer handheld = Db4o.openFile("handheld.yap");

		handheld.store(pilot1);
		handheld.store(pilot2);

		Configuration cfg = new Configuration().configure("f1/firststeps/hibernate.cfg.xml");
		ReplicationSession session = HibernateReplication.begin(handheld, cfg);

		ObjectSet changedInA = session.providerA().objectsChangedSinceLastReplication();
		while (changedInA.hasNext())
			session.replicate(changedInA.next());

		ObjectSet changedInB = session.providerB().objectsChangedSinceLastReplication();
		while (changedInB.hasNext())
			session.replicate(changedInB.next());

		session.commit();
		session.close();

		handheld.close();

		new File("handheld.yap").delete();
	}
}
