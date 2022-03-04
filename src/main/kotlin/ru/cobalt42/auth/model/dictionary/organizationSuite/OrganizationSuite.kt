package ru.cobalt42.auth.model.dictionary.organizationSuite

data class OrganizationSuite(
    var customer: Signer = Signer(),
    var mainConstruct: Signer = Signer(),
)
