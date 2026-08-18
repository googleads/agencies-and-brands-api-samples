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
import com.google.api.services.agenciesandbrands.v1.model.ListReportsResponse;
import com.google.api.services.agenciesandbrands.v1.model.Report;
import com.google.api.services.samples.agenciesandbrands.v1.Utils;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class ListReports {

  /**
   * Executes the list operation for reports.
   *
   * @param agenciesAndBrandsClient the initialized Agencies & Brands API client.
   * @param accountId the account ID of the agency whose reports to list.
   * @param pageSize the maximum number of items to return per page.
   * @param pageToken the page token from a previous response, if any.
   * @param filter a filter expression.
   * @param orderBy an order by expression.
   * @throws IOException if the API returns an error.
   */
  public static void execute(
      AgenciesAndBrands agenciesAndBrandsClient,
      Long accountId,
      Integer pageSize,
      String pageToken,
      String filter,
      String orderBy)
      throws IOException {
    String parent = String.format("agencies/%s", accountId);

    System.out.printf("Listing reports for agency account \"%s\".%n", parent);

    AgenciesAndBrands.Agencies.Reports.List request =
        agenciesAndBrandsClient.agencies().reports().list(parent);

    if (pageSize != null) {
      request.setPageSize(pageSize);
    }
    if (pageToken != null) {
      request.setPageToken(pageToken);
    }
    if (filter != null) {
      request.setFilter(filter);
    }
    if (orderBy != null) {
      request.setOrderBy(orderBy);
    }

    ListReportsResponse response = request.execute();
    List<Report> reports = response.getReports();

    if (reports == null || reports.isEmpty()) {
      System.out.println("No reports found.");
      return;
    }

    System.out.println("Successfully listed reports:");
    for (Report report : reports) {
      Utils.jsonPrettyPrint(report);
      System.out.println();
    }

    if (response.getNextPageToken() != null) {
      System.out.printf("Next page token: %s%n", response.getNextPageToken());
    }
  }

  /**
   * Creates and configures the ArgumentParser for this sample.
   *
   * @return the configured ArgumentParser.
   */
  private static ArgumentParser createArgumentParser() {
    ArgumentParser parser =
        ArgumentParsers.newFor("ListReports")
            .build()
            .defaultHelp(true)
            .description("Lists reports for the specified agency account.");

    // Required arguments.
    parser
        .addArgument("-a", "--account_id")
        .help("The account ID of the agency whose reports to list.")
        .required(true)
        .type(Long.class);

    // Optional arguments.
    parser
        .addArgument("--page_size")
        .help("The maximum number of reports to return per page.")
        .type(Integer.class);
    parser
        .addArgument("--page_token")
        .help("A page token, received from a previous `ListReports` call.");
    parser
        .addArgument("--filter")
        .help("An optional parameter used to filter the reports returned. Uses Cloud API list " +
            "filtering syntax. To learn more, see:" +
            "https://developers.google.com/ad-manager/api/beta/filters");
    parser
        .addArgument("--order_by")
        .help("An expression to order the results by. To learn more, see: " +
            "https://developers.google.com/ad-manager/api/beta/filters#order");

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
          parsedArgs.getInt("page_size"),
          parsedArgs.getString("page_token"),
          parsedArgs.getString("filter"),
          parsedArgs.getString("order_by"));
    } catch (IOException ex) {
      System.out.printf("Agencies & Brands API returned error response:%n%s", ex);
      System.exit(1);
    }
  }
}
