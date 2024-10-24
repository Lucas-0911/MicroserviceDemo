package com.company.accounts.service;

import com.company.accounts.model.dto.CustomerDto;
import com.company.accounts.model.form.CustomerForm;

public interface AccountsService {
    void createAccounts(CustomerDto customerDto);

    CustomerDto fetchAccount(String mobileNumber);


    boolean updateAccount(String mobileNumber, CustomerForm customerForm);

    boolean deleteAccount(String mobileNumber);
}
