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

package com.google.api.services.samples.agenciesandbrands.v1;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.agenciesandbrands.v1.AgenciesAndBrands;
import com.google.api.services.agenciesandbrands.v1.AgenciesAndBrandsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

/** Utilities used by the Agencies & Brands API samples. */
public class Utils {
  /**
   * Specify the name of your application. Suggested format is "MyCompany-ProductName/1.0". In
   * these samples, a default application name will be used.
   */
  private static final String APPLICATION_NAME =
      "Google-AgenciesAndBrandsApi-Samples/v1";

  /** Full path to JSON Key file - include file name */
  private static final java.io.File JSON_FILE = new java.io.File("INSERT_PATH_TO_JSON_FILE");

  /**
   * Global instance of a DateTimeFormatter used to parse LocalDate instances and convert them to
   * String.
   */
  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d");

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

  /**
   * Global instance of the maximum page size, which will be the default page size for samples with
   * pagination.
   */
  private static final Integer MAXIMUM_PAGE_SIZE = 50;

  /**
   * Authorizes the application to access the user's protected data.
   *
   * @throws IOException if the {@code JSON_FILE} can not be read.
   * @return An instantiated GoogleCredentials instance.
   */
  private static GoogleCredentials authorize() throws IOException {
    GoogleCredentials credentials;

    try (FileInputStream serviceAccountStream = new FileInputStream((JSON_FILE))) {
      Set<String> scopes = new HashSet<>(AgenciesAndBrandsScopes.all());

      credentials = ServiceAccountCredentials
          .fromStream(serviceAccountStream)
          .createScoped(scopes);
    }

    return credentials;
  }

  /**
   * Prints a given {@link GenericJson} model in human-readable JSON.
   *
   * <p>See {@link com.google.api.services.agenciesandbrands.v1.model} for models
   * representing objects used in the Agencies & Brands API.
   *
   * @param model A {@link GenericJson} instance.
   */
  public static void jsonPrettyPrint(GenericJson model) {
    if (model == null) {
      throw new NullPointerException("Provided model is null.");
    }

    try {
      System.out.println(JSON_FACTORY.toPrettyString(model));
    } catch (IOException e) {
      System.err.println("Error converting model to JSON: " + e.getMessage());
    }
  }

  /**
   * Retrieve a {@code DateTimeFormatter} instance used to parse and serialize {@code LocalDate}.
   *
   * @return An initialized {@code DateTimeFormatter} instance.
   */
  public static DateTimeFormatter getDateTimeFormatterForLocalDate() {
    return dateFormatter;
  }

  /**
   * Retrieve the default maximum page size.
   *
   * @return An Integer representing the default maximum page size for samples with pagination.
   */
  public static Integer getMaximumPageSize() {
    return MAXIMUM_PAGE_SIZE;
  }

  /**
   * Performs all necessary setup steps for running requests against the Agencies & Brands API.
   *
   * @return An initialized AgenciesAndBrands service object.
   */
  public static AgenciesAndBrands getAgenciesAndBrandsClient()
      throws IOException, GeneralSecurityException {
    GoogleCredentials credentials = authorize();
    HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
    HttpTransport httpTransport = new NetHttpTransport();

    return new AgenciesAndBrands.Builder(httpTransport, JSON_FACTORY, requestInitializer)
        .setApplicationName(APPLICATION_NAME)
        .build();
  }
}
