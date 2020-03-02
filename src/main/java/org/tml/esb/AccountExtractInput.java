package org.tml.esb;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountExtractInput
{
    @JsonProperty(required = true)
    private String invcNum;
    @JsonProperty(required = true)
    private String invoiceEndDate;
   
}
