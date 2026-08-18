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
import java.util.List;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class CreateReport {

  /**
   * Executes the create operation for a report.
   *
   * @param agenciesAndBrandsClient the initialized Agencies & Brands API client.
   * @param accountId the account ID of the agency that will create the report.
   * @param displayName the display name of the report to be created.
   * @param dimensions the list of dimensions to include in the report definition.
   * @param metrics the list of metrics to include in the report definition.
   * @param relativeDateRange the relative date range for the report.
   * @throws IOException if the API returns an error.
   */
  public static void execute(
      AgenciesAndBrands agenciesAndBrandsClient,
      Long accountId,
      String displayName,
      List<String> dimensions,
      List<String> metrics,
      String relativeDateRange)
      throws IOException {
    String parent = String.format("agencies/%s", accountId);

    System.out.printf("Creating Report \"%s\" for agency with name \"%s\".%n",
        displayName, parent);

    ReportDefinition reportDefinition =
        new ReportDefinition()
            .setDimensions(dimensions)
            .setMetrics(metrics)
            .setDateRange(new DateRange().setRelative(relativeDateRange));

    Report newReport =
        new Report()
            .setDisplayName(displayName)
            .setReportDefinition(reportDefinition);

    // Create a new report.
    Report createdReport =
        agenciesAndBrandsClient
            .agencies()
            .reports()
            .create(parent, newReport)
            .execute();

    System.out.println("Successfully created report:");
    Utils.jsonPrettyPrint(createdReport);
  }

  /**
   * Creates and configures the ArgumentParser for this sample.
   *
   * @return the configured ArgumentParser.
   */
  private static ArgumentParser createArgumentParser() {
    ArgumentParser parser =
        ArgumentParsers.newFor("CreateReport")
            .build()
            .defaultHelp(true)
            .description("Creates a new report for the given agency account.");

    // Required arguments.
    parser
        .addArgument("-a", "--account_id")
        .help("The account ID of the agency that will create the report.")
        .required(true)
        .type(Long.class);
    parser
        .addArgument("-d", "--display_name")
        .help("The display name of the report to be created.")
        .required(true);
    parser
        .addArgument("--dimensions")
        .help("The space-delimited list of dimensions to include in the report definition " +
            "(for example: DATE BUYER_NAME). For a listing of possible values, see: " +
            "https://developers.google.com/authorized-buyers/agencies-and-brands/apis/agenciesandbrands/reference/rest/v1/agencies.reports#dimension")
        .required(true)
        .nargs("+");
    parser
        .addArgument("--metrics")
        .help("The space-delimited list of metrics to include in the report definition " +
            "(for example: IMPRESSIONS CLICKS). For a listing of possible values, see: " +
            "https://developers.google.com/authorized-buyers/agencies-and-brands/apis/agenciesandbrands/reference/rest/v1/agencies.reports#metric")
        .required(true)
        .nargs("+");

    // Optional arguments.
    parser
        .addArgument("--relative_date_range")
        .help("The relative date range for the report to include in the report definition. (for example: LAST_7_DAYS, TODAY, YESTERDAY). " +
            "Defaults to LAST_7_DAYS if unspecified. For a listing of possible values, see: " +
            "https://developers.google.com/authorized-buyers/agencies-and-brands/apis/agenciesandbrands/reference/rest/v1/agencies.reports#relativedaterange")
        .setDefault("LAST_7_DAYS");

    return parser;
  }

  public static void main(String[] args) {
    ArgumentParser parser = createArgumentParser();

    Namespace parsedArgs = null;
    try {
      parsedArgs = parser.parseArgs(args);
    } catch (ArgumentParserException ex) {
      parser.handleError(ex);
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
    String displayName = parsedArgs.getString("display_name");
    List<String> dimensions = parsedArgs.getList("dimensions");
    List<String> metrics = parsedArgs.getList("metrics");
    String relativeDateRange = parsedArgs.getString("relative_date_range");

    try {
      execute(client, accountId, displayName, dimensions, metrics, relativeDateRange);
    } catch (IOException ex) {
      System.out.printf("Agencies & Brands API returned error response:%n%s", ex);
      System.exit(1);
    }
  }
}
