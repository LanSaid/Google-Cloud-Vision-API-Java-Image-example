/**
 * Created by АленкаиВова on 19.05.2016.
 */

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.common.collect.ImmutableList;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.List;

public class GVisionQuickstart {
    /** Application name. */
    private static final String APPLICATION_NAME = "Robot_test_1";

    private static final String IMAGES_PATH = "IMAGES_PATH";

    private static final int MAX_LABELS = 3;

       /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        if (args.length != 1) {
            System.err.println("Missing imagePath argument.");
            System.err.println("Usage:");
            System.err.printf("\tjava %s imagePath\n", GVisionQuickstart.class.getCanonicalName());
            System.exit(1);
        }
        String imagePath = args[0];

        GVisionQuickstart app = new GVisionQuickstart(getVisionService());
        printLabels(System.out, app.labelImage(imagePath, MAX_LABELS));
    }
    public static void printLabels(PrintStream out, Path imagePath, List<EntityAnnotation> labels) {
        out.printf("Labels for image %s:\n", imagePath);
        for (EntityAnnotation label : labels) {
            out.printf(
                    "\t%s (score: %.3f)\n",
                    label.getDescription(),
                    label.getScore());
        }
        if (labels.isEmpty()) {
            out.println("\tNo labels found.");
        }
    }


    public static Vision getVisionService() throws IOException, GeneralSecurityException {
        GoogleCredential credential = GoogleCredential.getApplicationDefault().createScoped(VisionScopes.all());
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        return new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private final Vision vision;


    public GVisionQuickstart(Vision vision) {
        this.vision = vision;
    }

    public List <EntityAnnotation> labelImage(Path path, int maxResults) throws IOException {
        // [START construct_request]
        ListFilesUtils listFile = new ListFilesUtils(IMAGES_PATH);

        for (int i = 0; i < listFile.listFileNames.size(); i++) {



        byte[] data = Files.readAllBytes(path);

        AnnotateImageRequest request =
                new AnnotateImageRequest()
                        .setImage(new Image().encodeContent(data))
                        .setFeatures(ImmutableList.of(
                                new Feature()
                                        .setType("LABEL_DETECTION")
                                        .setMaxResults(maxResults)));
        long startTime = System.currentTimeMillis();

        Vision.Images.Annotate annotate =
                vision.images()
                        .annotate(new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request)));
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        // annotate.setDisableGZipContent(true);
        // [END construct_request]

        // [START parse_response]
        BatchAnnotateImagesResponse batchResponse = annotate.execute();
        assert batchResponse.getResponses().size() == 1;
        AnnotateImageResponse response = batchResponse.getResponses().get(0);
        if (response.getLabelAnnotations() == null) {
            throw new IOException(
                    response.getError() != null
                            ? response.getError().getMessage()
                            : "Unknown error getting image annotations");
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Total elapsed http request/response time in milliseconds: " + elapsedTime);

        return response.getLabelAnnotations();
        // [END parse_response]

        }
    }
}

