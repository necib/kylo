package com.thinkbiganalytics.jobrepo.query.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by sr186054 on 8/28/15.
 */
public class DataConfidenceSummary {

  List<CheckDataJob> latestCheckDataFeeds;
  private Float confidencePercentage = 0f;
  private Integer failedCount = 0;
  private Integer successCount = 0;
  private Integer totalCount = 0;
  private Date minCompleteDate;
  List<CheckDataJob> failedJobs;
  private String timeSinceLastCheck; // in minutes

  private List<String> warnings;
  private Integer warningTimeThreshold; // in minutes

  public DataConfidenceSummary() {

  }

  public DataConfidenceSummary(List<CheckDataJob> latestCompletedFeeds, Integer warningTimeThreshold) {
    failedJobs = new ArrayList<CheckDataJob>();
    this.latestCheckDataFeeds = latestCompletedFeeds;
    this.warningTimeThreshold = warningTimeThreshold;
    this.warnings = new ArrayList<String>();
    parse();
  }

  @JsonIgnore
  private void parse() {
    if (latestCheckDataFeeds != null && !latestCheckDataFeeds.isEmpty()) {
      this.totalCount = latestCheckDataFeeds.size();
      Date minCompleteDate = null;
      for (CheckDataJob job : latestCheckDataFeeds) {
        if (minCompleteDate == null || minCompleteDate.getTime() < job.getEndTime().getMillis()) {
          minCompleteDate = job.getEndTime().toDate();
        }
        if (job.isValid()) {
          this.successCount++;
        } else {
          this.failedCount++;
          this.failedJobs.add(job);
          warnings
              .add(job.getFeedName() + " failed Validation at " + job.getEndTime() + " because: " + job.getValidationMessage());
        }
      }
      Date now = new Date();
      long duration = now.getTime() - minCompleteDate.getTime();
      long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);

      this.timeSinceLastCheck = diffInMinutes + " min ";
      if (diffInMinutes == 0) {
        long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        this.timeSinceLastCheck = diffInSeconds + " sec ";
      }
      this.confidencePercentage = ((float) successCount / totalCount) * 100;
      if (this.confidencePercentage > 0) {
        DecimalFormat twoDForm = new DecimalFormat("##.##");
        this.confidencePercentage = Float.valueOf(twoDForm.format(this.confidencePercentage));
      }
      if (this.warningTimeThreshold != null && diffInMinutes > this.warningTimeThreshold) {
        warnings.add(" Warning it has been over " + this.warningTimeThreshold
                     + " minutes since some of the check data jobs have been run");
      }
      this.minCompleteDate = minCompleteDate;
    } else {
      warnings.add("No Check Data Jobs Found");

    }
  }

  public Float getConfidencePercentage() {
    return confidencePercentage;
  }

  public Integer getFailedCount() {
    return failedCount;
  }

  public Integer getSuccessCount() {
    return successCount;
  }

  public Integer getTotalCount() {
    return totalCount;
  }

  public Date getMinCompleteDate() {
    return minCompleteDate;
  }

  public String getTimeSinceLastCheck() {
    return timeSinceLastCheck;
  }

  public List<String> getWarnings() {
    return warnings;
  }

  public Integer getWarningTimeThreshold() {
    return warningTimeThreshold;
  }

  public List<CheckDataJob> getLatestCheckDataFeeds() {
    return latestCheckDataFeeds;
  }

  public void setLatestCheckDataFeeds(List<CheckDataJob> latestCheckDataFeeds) {
    this.latestCheckDataFeeds = latestCheckDataFeeds;
  }

  public List<CheckDataJob> getFailedJobs() {
    return failedJobs;
  }

  public void setFailedJobs(List<CheckDataJob> failedJobs) {
    this.failedJobs = failedJobs;
  }
}