/*
 * Copyright (c) 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.services.samples.agenciesandbrands.v1.agencies.reports;

import com.google.api.services.agenciesandbrands.v1.AgenciesAndBrands;
import com.google.api.services.agenciesandbrands.v1.model.DateRange;
import com.google.api.services.agenciesandbrands.v1.model.Report;
import com.google.api.services.agenciesandbrands.v1.model.ReportDefinition;
import com.google.api.services.samples.agenciesandbrands.v1.Utils;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class PatchReport {

  /**
   * Executes the patch operation for a report.
   *
   * @param agenciesAndBrandsClient the initialized Agencies & Brands API client.
   * @param accountId the account ID of the agency that created the report.
   * @param reportId the resource ID of the report to update.
   * @param displayName the modified display name for the report (or null if not being modified).
   * @param relativeDateRange the modified relative date range for the report definition (or null
   *     if not being modified).
   * @param dimensions the modified list of dimensions for the report definition (or null if not
   *     being modified).
   * @param metrics the modified list of metrics for the report definition (or null if not being
   *     modified).
   * @throws IOException if the API returns an error.
   */
  public static void execute(
      AgenciesAndBrands agenciesAndBrandsClient,
      Long accountId,
      String reportId,
      String displayName,
      String relativeDateRange,
      List<String> dimensions,
      List<String> metrics)
      throws IOException {

    String name = String.format("agencies/%d/reports/%s", accountId, reportId);

    List<String> patchedFields = new ArrayList<>();
    Report patchedReport = new Report();

    if (displayName != null) {
      patchedReport.setDisplayName(displayName);
      patchedFields.add("displayName");
    }

    ReportDefinition reportDefinition = new ReportDefinition();
    if (relativeDateRange != null) {
      reportDefinition.setDateRange(new DateRange().setRelative(relativeDateRange));
      patchedFields.add("reportDefinition.dateRange.relative");
    }
    if (dimensions != null) {
      reportDefinition.setDimensions(dimensions);
      patchedFields.add("reportDefinition.dimensions");
    }
    if (metrics != null) {
      reportDefinition.setMetrics(metrics);
      patchedFields.add("reportDefinition.metrics");
    }
    // Set the ReportDefinition on the patched Report unconditionally. Any fields not
    // explicitly specified in the updateMask will be ignored by the API.
    patchedReport.setReportDefinition(reportDefinition);

    if (patchedFields.isEmpty()) {
      System.out.println("No fields specified to update.");
      return;
    }

    String updateMask = String.join(",", patchedFields);

    System.out.printf("Patching report with name \"%s\".%n", name);

    // Patch the report.
    Report returnedReport =
        agenciesAndBrandsClient
            .agencies()
            .reports()
            .patch(name, patchedReport)
            .setUpdateMask(updateMask)
            .execute();

    System.out.println("Successfully patched report:");
    Utils.jsonPrettyPrint(returnedReport);
  }

  /**
   * Creates and configures the ArgumentParser for this sample.
   *
   * @return the configured ArgumentParser.
   */
  private static ArgumentParser createArgumentParser() {
    ArgumentParser parser =
        ArgumentParsers.newFor("PatchReport")
            .build()
            .defaultHelp(true)
            .description("Updates a specified report.");

    // Required arguments.
    parser
        .addArgument("-a", "--account_id")
        .help("The account ID of the agency that created the report.")
        .required(true)
        .type(Long.class);
    parser
        .addArgument("-r", "--report_id")
        .help("The resource ID of the report to update.")
        .required(true);

    // Optional arguments.
    parser
        .addArgument("-d", "--display_name")
        .help("The modified display name of the report.");
    parser
        .addArgument("--relative_date_range")
        .help("The modified relative date range for the report definition (for example: " +
            "LAST_7_DAYS, TODAY, YESTERDAY). For a listing of possible values, see: " +
            "https://developers.google.com/authorized-buyers/agencies-and-brands/apis/agenciesandbrands/reference/rest/v1/agencies.reports#relativedaterange");
    parser
        .addArgument("--dimensions")
        .help("A space-delimited list of modified dimensions for the report definition (for " +
            "example: DATE, CAMPAIGN_ID). For a listing of possible values, see: " +
            "https://developers.google.com/authorized-buyers/agencies-and-brands/apis/agenciesandbrands/reference/rest/v1/agencies.reports#dimension")
        .nargs("+");
    parser
        .addArgument("--metrics")
        .help("A space-delimited list of modified metrics for the report definition (for " +
            "example: IMPRESSIONS CLICKS). For a listing of possible values, see: " +
            "https://developers.google.com/authorized-buyers/agencies-and-brands/apis/agenciesandbrands/reference/rest/v1/agencies.reports#metric")
        .nargs("+");

    return parser;
  }

  public static void main(String[] args) {
    ArgumentParser parser = createArgumentParser();
    Namespace parsedArgs = null;
    try {
      parsedArgs = parser.parseArgs(args);
    } catch (ArgumentParserException e) {
      parser.handleError(e);
      System.exit(1);
    }

    AgenciesAndBrands client = null;
    try {
      client = Utils.getAgenciesAndBrandsClient();
    } catch (IOException ex) {
      System.out.printf("Unable to create Agencies & Brands API service:%n%s", ex);
      System.out.println("Did you specify a valid path to a service account key file?");
      System.exit(1);
    } catch (GeneralSecurityException ex) {
      System.out.printf("Unable to establish secure HttpTransport:%n%s", ex);
      System.exit(1);
    }

    Long accountId = parsedArgs.getLong("account_id");
    String reportId = parsedArgs.getString("report_id");
    String displayName = parsedArgs.getString("display_name");
    String relativeDateRange = parsedArgs.getString("relative_date_range");
    List<String> dimensions = parsedArgs.getList("dimensions");
    List<String> metrics = parsedArgs.getList("metrics");

    try {
      execute(
          client, accountId, reportId, displayName, relativeDateRange, dimensions, metrics);
    } catch (IOException e) {
      System.out.printf("Agencies & Brands API returned error response:%n%s", e);
      System.exit(1);
    }
  }
}
