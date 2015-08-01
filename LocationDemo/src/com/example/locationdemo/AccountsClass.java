package com.example.locationdemo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AccountsClass extends Fragment {

	TextView accountsGoogle;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.layout_fragmentc, container,
				false);
		accountsGoogle = (TextView) view.findViewById(R.id.textViewAccounts);
		displayGoogleAccounts();

		return view;
	}

	/**
	 * Gets Device's all Google Accounts and sets to the textview
	 */
	private void displayGoogleAccounts() {
		AccountManager manager = AccountManager.get(getActivity());
		Account[] accounts = manager.getAccountsByType("com.google");
		String googleAccounts = "";
		for (int i = 0; i < accounts.length; i++) {
			googleAccounts += "\n\n" + (i + 1) + ". " + accounts[i].name
					+ " - " + accounts[i].type;
		}
		accountsGoogle.setText(googleAccounts);
	}
}