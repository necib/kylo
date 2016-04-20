package com.thinkbiganalytics.nifi.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by sr186054 on 1/22/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NifiProcessorDTO {
    private String processorName;
    private String processorId;
    private String processGroupName;
    private String processGroupId;
    private Collection<NifiError> validationErrors;
    public NifiProcessorDTO() {

    }

    public NifiProcessorDTO(String processorName, String processorId, String processGroupName, String processGroupId) {
        this.processorName = processorName;
        this.processorId = processorId;
        this.processGroupName = processGroupName;
        this.processGroupId = processGroupId;
    }
    public NifiProcessorDTO(String processorName, String processorId, String processGroupId) {
        this.processorName = processorName;
        this.processorId = processorId;
        this.processGroupName = processGroupName;
        this.processGroupId = processGroupId;
    }
    public String getProcessorName() {
        return processorName;
    }

    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }

    public String getProcessorId() {
        return processorId;
    }

    public void setProcessorId(String processorId) {
        this.processorId = processorId;
    }

    public String getProcessGroupName() {
        return processGroupName;
    }

    public void setProcessGroupName(String processGroupName) {
        this.processGroupName = processGroupName;
    }

    public String getProcessGroupId() {
        return processGroupId;
    }

    public void setProcessGroupId(String processGroupId) {
        this.processGroupId = processGroupId;
    }

    public Collection<NifiError> getValidationErrors() {
        if(validationErrors == null){
            validationErrors = new ArrayList<>();
        }
        return validationErrors;
    }

    public void setValidationErrors(Collection<NifiError> validationErrors) {
        this.validationErrors = validationErrors;
    }


    @JsonIgnore
    public void addValidationErrors(Collection<String> validationErrors) {
        if(validationErrors != null && !validationErrors.isEmpty()) {
            for(String error: validationErrors){
                addError(error);
            }
        }
    }
    @JsonIgnore
    public void addError(NifiError.SEVERITY severity,String error, String category){
        getValidationErrors().add(new NifiError(severity,error, category));
    }
    @JsonIgnore
    public void addError(NifiError error){
        getValidationErrors().add(error);
    }
    @JsonIgnore
    public void addError(String error){
        getValidationErrors().add(new NifiError(error));
    }
    @JsonIgnore
    public List<NifiError> getFatalErrors(){
        List<NifiError> errors = null;
        if(validationErrors != null && !validationErrors.isEmpty()){
            errors = Lists.newArrayList(Iterables.filter(validationErrors, new Predicate<NifiError>() {
                @Override
                public boolean apply(NifiError nifiError) {
                   return NifiError.SEVERITY.FATAL.equals(nifiError.getSeverity());
                }
            }));
        }
        return errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NifiProcessorDTO that = (NifiProcessorDTO) o;

        if (!processorId.equals(that.processorId)) return false;
        return processGroupId.equals(that.processGroupId);

    }

    @Override
    public int hashCode() {
        int result = processorId.hashCode();
        result = 31 * result + processGroupId.hashCode();
        return result;
    }
}