package org.omar.config;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@OpenAPIDefinition(
        info = @Info(
                title = "Sakila Film API",
                version = "1.0.0",
                description = """
                        REST API to browse and manage the Sakila film catalog.
                        Built with Quarkus, JPAStreamer and MySQL.
                        All errors follow the same `ErrorResponse` format.""",
                contact = @Contact(
                        name = "Your Name",
                        email = "you@example.com",
                        url = "https://github.com/your-handle"),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html")),
        tags = {
                @Tag(name = "Films", description = "Browse and manage the Sakila film catalog")
        },
        externalDocs = @ExternalDocumentation(
                description = "Sakila sample database",
                url = "https://dev.mysql.com/doc/sakila/en/"))
public class OpenApiConfig extends Application {
}