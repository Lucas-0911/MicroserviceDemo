package com.company.accounts.model.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountForm {
    @NotEmpty(message = "Account type can not be a null or empty.")
    private String accountType;

    @NotEmpty(message = "Branch address can not be a null or empty.")
    private String branchAddress;
}
