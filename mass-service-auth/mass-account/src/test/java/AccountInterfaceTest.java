import junit.textui.TestRunner;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@ContextConfiguration
public class AccountInterfaceTest {


    private AccountInterfaceTest() {
    }

    public static AccountInterfaceTest createAccountInterfaceTest() {
        return new AccountInterfaceTest();
    }
}
