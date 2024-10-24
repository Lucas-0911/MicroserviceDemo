package com.company.accounts.service;

import com.company.accounts.constants.AccountsConstants;
import com.company.accounts.exception.CustomerAlreadyExitsException;
import com.company.accounts.exception.ResourceNotFoundException;
import com.company.accounts.mapper.AccountsMapper;
import com.company.accounts.mapper.CustomerMapper;
import com.company.accounts.model.dto.AccountsDto;
import com.company.accounts.model.dto.CustomerDto;
import com.company.accounts.model.entity.Accounts;
import com.company.accounts.model.entity.Customer;
import com.company.accounts.model.form.CustomerForm;
import com.company.accounts.repository.AccountsRepository;
import com.company.accounts.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AccountsServiceImpl implements AccountsService {

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public void createAccounts(CustomerDto customerDto) {

        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Optional<Customer> exitCustomer = customerRepository.findByMobileNumber(customerDto.getMobileNumber());

        if (exitCustomer.isPresent()) {
            throw new CustomerAlreadyExitsException("Customer already exits with given mobile number: " + customerDto.getMobileNumber());
        }

        customer.setCreatedAt(LocalDateTime.now());
        customer.setCreatedBy("Lucas");
        //Save customer
        Customer newCustomer = customerRepository.save(customer);

        Long randAccNumber = 1000000000L + new Random().nextInt(900000000);

        Accounts account = new Accounts();
        account.setCustomerId(newCustomer.getCustomerId());
        account.setAccountNumber(randAccNumber);
        account.setAccountType(AccountsConstants.SAVINGS);
        account.setBranchAddress(AccountsConstants.ADDRESS);
        account.setCreatedAt(LocalDateTime.now());
        account.setCreatedBy("Lucas");
        //Save account
        accountsRepository.save(account);
    }

    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );

        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Accounts", "Customer id", customer.getCustomerId().toString())
        );

        AccountsDto accountsDto = AccountsMapper.mapToAccountsDto(accounts, new AccountsDto());

        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        customerDto.setAccounts(accountsDto);

        return customerDto;
    }

    @Override
    public boolean updateAccount(String mobileNumber, CustomerForm customerForm) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Accounts", "Customer id", customer.getCustomerId().toString())
        );

        customer.setEmail(customerForm.getEmail());
        customer.setName(customer.getName());
        customer.setUpdatedAt(LocalDateTime.now());
        customer.setUpdatedBy("Lucas");

        accounts.setAccountType(customerForm.getAccount().getAccountType());
        accounts.setBranchAddress(customerForm.getAccount().getBranchAddress());
        accounts.setUpdatedAt(LocalDateTime.now());
        accounts.setUpdatedBy("Lucas");

        customerRepository.save(customer);
        accountsRepository.save(accounts);

        return true;
    }


    @Transactional
    @Override
    public boolean deleteAccount(String mobileNumber) {

        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );

        accountsRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }

}
