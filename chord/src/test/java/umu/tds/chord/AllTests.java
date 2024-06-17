package umu.tds.chord;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ControllerTest.class, SongRepositoryTest.class, UserRepositoryTest.class, UserTest.class })
public class AllTests {

}
