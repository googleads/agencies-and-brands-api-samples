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
import com.google.api.services.agenciesandbrands.v1.model.Operation;
import com.google.api.services.agenciesandbrands.v1.model.RunReportRequest;
import com.google.api.services.samples.agenciesandbrands.v1.Utils;
import java.io.IOException;
import java.security.GeneralSecurityException;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class RunReport {

  /**
   * Executes the run operation for a report.
   *
   * @param agenciesAndBrandsClient the initialized Agencies & Brands API client.
   * @param accountId the account ID of the agency that created the report.
   * @param reportId the resource ID of the report to run.
   * @throws IOException if the API returns an error.
   */
  public static void execute(
      AgenciesAndBrands agenciesAndBrandsClient, Long accountId, String reportId)
      throws IOException {
    String name = String.format("agencies/%s/reports/%s", accountId, reportId);

    System.out.printf("Running report with name \"%s\".%n", name);

    RunReportRequest requestBody = new RunReportRequest();

    // Run the specified report to start an asynchronous operation.
    Operation operation =
        agenciesAndBrandsClient
            .agencies()
            .reports()
            .run(name, requestBody)
            .execute();

    System.out.println("Successfully initiated report run operation:");
    Utils.jsonPrettyPrint(operation);
  }

  /**
   * Creates and configures the ArgumentParser for this sample.
   *
   * @return the configured ArgumentParser.
   */
  private static ArgumentParser createArgumentParser() {
    ArgumentParser parser =
        ArgumentParsers.newFor("RunReport")
            .build()
            .defaultHelp(true)
            .description("Runs a specified report asynchronously, returning an Operation " +
                "that can be used to track its progress.");

    // Required arguments.
    parser
        .addArgument("-a", "--account_id")
        .help("The account ID of the agency that created the report.")
        .required(true)
        .type(Long.class);
    parser
        .addArgument("-r", "--report_id")
        .help("The resource ID of the report to run.")
        .required(true);

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

    try {
      execute(
          client,
          parsedArgs.getLong("account_id"),
          parsedArgs.getString("report_id"));
    } catch (IOException ex) {
      System.out.printf("Agencies & Brands API returned error response:%n%s", ex);
      System.exit(1);
    }
  }
}
