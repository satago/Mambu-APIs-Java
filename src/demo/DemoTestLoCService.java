package demo;

import java.util.List;

import com.mambu.apisdk.MambuAPIFactory;
import com.mambu.apisdk.exception.MambuApiException;
import com.mambu.apisdk.services.LinesOfCreditService;
import com.mambu.linesofcredit.shared.model.AccountsFromLineOfCredit;
import com.mambu.linesofcredit.shared.model.LineOfCredit;
import com.mambu.loans.shared.model.LoanAccount;
import com.mambu.savings.shared.model.SavingsAccount;

/**
 * Test class to show example usage for Line Of Credit (LoC) API
 * 
 * @author mdanilkis
 * 
 */
public class DemoTestLoCService {

	public static void main(String[] args) {

		DemoUtil.setUp();

		try {

			testGetLinesOfCredit(); // Available since 3.11

			testGetCustomerLinesOfCredit(); // Available since 3.11

			// test get accounts for LoC
			AccountsFromLineOfCredit locAccounts = testGetAccountsForLineOfCredit();// Available since 3.11

			// test add and remove accounts from LoC.
			testAddAndRemoveAccountsForLineOfCredit(locAccounts);// Available since 3.12.2

		} catch (MambuApiException e) {
			System.out.println("Exception caught in Demo Test Lines of Credit Service");
			System.out.println("Error code=" + e.getErrorCode());
			System.out.println(" Cause=" + e.getCause() + ".  Message=" + e.getMessage());
		}

	}

	/**
	 * Test Get paginated list of all lines of credit and LoC details
	 * 
	 * @throws MambuApiException
	 */
	public static void testGetLinesOfCredit() throws MambuApiException {
		System.out.println("\nIn testGetLinesOfCredit");

		LinesOfCreditService linesOfCreditService = MambuAPIFactory.getLineOfCreditService();
		Integer offset = 0;
		Integer limit = 30;

		// Test getting all lines of credit
		List<LineOfCredit> linesOfCredit = linesOfCreditService.getAllLinesOfCredit(offset, limit);
		System.out.println("Total Lines of Credit=" + linesOfCredit.size());
		if (linesOfCredit.size() == 0) {
			System.out.println("*** No Lines of Credit to test ***");
			return;
		}
		// Test get Line Of Credit details
		String lineofcreditId = linesOfCredit.get(0).getId();
		System.out.println("Getting details for Line of Credit ID=" + lineofcreditId);
		LineOfCredit lineOfCredit = linesOfCreditService.getLineOfCredit(lineofcreditId);
		// Log returned LoC
		System.out.println("Line of Credit. ID=" + lineOfCredit.getId() + "\tAmount=" + lineOfCredit.getAmount()
				+ "\tOwnerType=" + lineOfCredit.getOwnerType() + "\tHolderKey="
				+ lineOfCredit.getAccountHolder().getAccountHolderKey());
	}

	/**
	 * Test Get lines of credit for a Client and Group
	 * 
	 * @return any of the LoC IDs
	 */
	public static void testGetCustomerLinesOfCredit() throws MambuApiException {
		System.out.println("\nIn testGetCustomerLinesOfCredit");

		LinesOfCreditService linesOfCreditService = MambuAPIFactory.getLineOfCreditService();
		Integer offset = 0;
		Integer limit = 30;
		// Test Get line of credit for a Client
		// Get Demo Client ID first

		final String clientId = DemoUtil.getDemoClient().getId();
		// Get Lines of Credit for a client
		List<LineOfCredit> clientLoCs = linesOfCreditService.getClientLinesOfCredit(clientId, offset, limit);
		System.out.println(clientLoCs.size() + " lines of credit  for Client " + clientId);

		// Test Get line of credit for a Group
		// Get Demo Group ID first
		final String groupId = DemoUtil.getDemoGroup().getId();
		// Get Lines of Credit for a group
		List<LineOfCredit> groupLoCs = linesOfCreditService.getGroupLinesOfCredit(groupId, offset, limit);
		System.out.println(groupLoCs.size() + " lines of credit for Group " + groupId);

	}

	/**
	 * Test Get Accounts for a line of Credit
	 * 
	 * @param lineOfCreditId
	 *            an id or encoded key for a Line of Credit
	 * @return accounts for a line of credit
	 * @throws MambuApiException
	 */
	public static AccountsFromLineOfCredit testGetAccountsForLineOfCredit() throws MambuApiException {
		System.out.println("\nIn testGetAccountsForLineOfCredit");
		// Test Get Accounts for a line of credit

		String lineOfCreditId = DemoUtil.demoLineOfCreditId;
		if (lineOfCreditId == null) {
			System.out.println("WARNING: No Demo Line of credit defined");
			return null;
		}

		LinesOfCreditService linesOfCreditService = MambuAPIFactory.getLineOfCreditService();

		System.out.println("\nGetting all accounts for LoC with ID= " + lineOfCreditId);
		AccountsFromLineOfCredit accountsForLoC = linesOfCreditService.getAccountsForLineOfCredit(lineOfCreditId);
		// Log returned results
		List<LoanAccount> loanAccounts = accountsForLoC.getLoanAccounts();
		List<SavingsAccount> savingsAccounts = accountsForLoC.getSavingsAccounts();
		System.out.println("Total Loan Accounts=" + loanAccounts.size() + "\tTotal Savings Accounts="
				+ savingsAccounts.size() + " for LoC=" + lineOfCreditId);

		return accountsForLoC;
	}

	// Test deleting and adding accounts to a Line of Credit
	public static void testAddAndRemoveAccountsForLineOfCredit(AccountsFromLineOfCredit accountsForLoC)
			throws MambuApiException {
		System.out.println("\nIn testAddAndRemoveAccountsForLineOfCredit");

		String lineOfCreditId = DemoUtil.demoLineOfCreditId;
		if (lineOfCreditId == null) {
			System.out.println("WARNING: No Demo Line of credit defined");
			return;
		}

		// Test remove and add for Loan Accounts
		List<LoanAccount> loanAccounts = accountsForLoC.getLoanAccounts();
		testdeleteAndAddLoanAccounts(lineOfCreditId, loanAccounts);

		// Test remove and add for Savings Accounts
		List<SavingsAccount> savingsAccounts = accountsForLoC.getSavingsAccounts();
		testdeleteAndAddSavingsAccounts(lineOfCreditId, savingsAccounts);
	}

	// For each Loan account associated with a credit line test deleting and adding it back
	private static void testdeleteAndAddLoanAccounts(String lineOfCreditId, List<LoanAccount> accounts)
			throws MambuApiException {
		System.out.println("\nIn testdeleteAndAddLoanAccounts for LoC=" + lineOfCreditId);

		LinesOfCreditService linesOfCreditService = MambuAPIFactory.getLineOfCreditService();
		String accountId = null;
		if (accounts == null || accounts.size() == 0) {
			System.out.println("WARNING: no Loan Account to remove for LoC=" + lineOfCreditId);
		} else {
			// We have assigned accounts. Test remove and then test adding it back
			// Test removing Loan Account. Accounts requiring LoC cannot be removed, so try until can
			for (LoanAccount account : accounts) {
				accountId = account.getId();
				try {
					boolean deleted = linesOfCreditService.deleteLoanAccount(lineOfCreditId, accountId);
					System.out.println("Removed Status=" + deleted + "\tAccount with ID=" + accountId
							+ " deleted from LoC=" + lineOfCreditId);
					// Deleted OK, now add the same back
					LoanAccount addedLoan = linesOfCreditService.addLoanAccount(lineOfCreditId, accountId);
					System.out.println("Added Loan Account with ID=" + addedLoan.getId() + " to LoC=" + lineOfCreditId);
				} catch (MambuApiException e) {
					System.out.println("Failed to remove account " + accountId + "\tMessage=" + e.getErrorMessage());
				}

			}

		}
	}

	// For each Savings account associated with a credit line test deleting and adding it back
	private static void testdeleteAndAddSavingsAccounts(String lineOfCreditId, List<SavingsAccount> accounts)
			throws MambuApiException {
		System.out.println("\nIn testdeleteAndAddSavingsAccounts for LoC=" + lineOfCreditId);

		LinesOfCreditService linesOfCreditService = MambuAPIFactory.getLineOfCreditService();
		String accountId = null;
		if (accounts == null || accounts.size() == 0) {
			System.out.println("WARNING: no Savings Account to remove for LoC=" + lineOfCreditId);
		} else {
			// We have assigned accounts. Test remove and then test adding it back
			// Test removing Savings Account. Accounts requiring LoC cannot be removed, so try until can
			for (SavingsAccount account : accounts) {
				accountId = account.getId();
				try {
					boolean deleted = linesOfCreditService.deleteSavingsAccount(lineOfCreditId, accountId);
					System.out.println("Removed Status=" + deleted + "\tAccount with ID=" + accountId
							+ " deleted from LoC=" + lineOfCreditId);
					;
					// Deleted OK, now add the same back
					SavingsAccount addedAccount = linesOfCreditService.addSavingsAccount(lineOfCreditId, accountId);
					System.out.println("Added Savings Account with ID=" + addedAccount.getId() + " to LoC="
							+ lineOfCreditId);
				} catch (MambuApiException e) {
					System.out.println("Failed to remove account " + accountId + "\tMessage=" + e.getErrorMessage());
				}

			}

		}
	}
}
