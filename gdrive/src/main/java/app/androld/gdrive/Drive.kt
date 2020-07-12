/*
 * Copyright (c) 2020 Roman Tsarou
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.androld.gdrive

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import org.apache.tika.Tika
import java.io.File


object Drive {
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private val SCOPES = listOf(DriveScopes.DRIVE_FILE)
    private const val APPLICATION_NAME = "gdrive"
    private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
    private lateinit var service: Drive
    private val tika = Tika()

    fun init(
        credentialsFilePath: String,
        tokensDirectoryPath: String = "gdrive${File.pathSeparator}tokens"
    ) {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        service = Drive.Builder(
            httpTransport,
            JSON_FACTORY,
            getCredentials(
                credentialsFilePath,
                tokensDirectoryPath,
                httpTransport
            )
        )
            .setApplicationName(APPLICATION_NAME)
            .build()
    }

    private fun getCredentials(
        credentialsFilePath: String,
        tokensDirectoryPath: String,
        httpTransport: NetHttpTransport
    ): Credential {
        // Load client secrets.
        val reader = File(credentialsFilePath).reader()
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, reader)

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
            httpTransport, JSON_FACTORY, clientSecrets, SCOPES
        )
            .setDataStoreFactory(FileDataStoreFactory(File(tokensDirectoryPath)))
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    fun uploadFile(
        driveFolderId: String,
        file: File,
        mimeType: String? = tika.detect(file.name)
    ): String {
        if (!file.exists()) error("File $file not exists.")

        val fileMetadata = com.google.api.services.drive.model.File()
        fileMetadata.name = file.name
        fileMetadata.parents = listOf(driveFolderId)
        val mediaContent = FileContent(mimeType, file)
        val create = service.files().create(fileMetadata, mediaContent)
            .setFields("id, parents")
            .execute()
        return "https://drive.google.com/file/d/${create.id}/view"
    }

}