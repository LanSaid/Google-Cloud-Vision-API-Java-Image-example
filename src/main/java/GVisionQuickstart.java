/**
 * Created by АленкаиВова on 19.05.2016.
 * @author vmetenev
 * @version
 * @since JDK1.6+
 */

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
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

@SuppressWarnings("serial")

public class GVisionQuickstart {
    /** Application name. */

    private static final String APPLICATION_NAME = "Robot_test_1";
    private static final int MAX_LABELS = 5;
    private final Vision vision;

    public GVisionQuickstart(Vision vision) {
        this.vision = vision;
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        if (args.length != 1) {
            System.err.println("Missing imagePath argument.");
            System.err.println("Usage:");
            System.err.printf("\tjava %s imagePath\n", GVisionQuickstart.class.getCanonicalName());
            System.exit(1);
        }
        String imagePath = args[0];

        GVisionQuickstart app = new GVisionQuickstart(getVisionService());

        printLabels(System.out, imagePath, app.labelImage(imagePath, MAX_LABELS));
    }

    private static void printLabels(PrintStream out, String imagePath, List<EntityAnnotation> labels) {
        Path imagesPath = Paths.get(imagePath);
        out.printf("Labels for image %s:\n", imagesPath);
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

    private static Vision getVisionService() throws IOException, GeneralSecurityException {
        GoogleCredential credential = GoogleCredential.getApplicationDefault().createScoped(VisionScopes.all());
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        return new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public ImmutableList<EntityAnnotation> labelImage(String path, int maxResults) throws IOException {
        ImmutableList.Builder<AnnotateImageRequest> requests = ImmutableList.builder();
        ListFilesUtils listFile = new ListFilesUtils(path);
        listFile.listFiles();

        try {

            for (int i = 0; i < listFile.listFileNames.size(); i++) {
                 // System.out.println(listFile.listFileNames.get(i));
                //
                Path imgPath = Paths.get((String) listFile.listFileNames.get(i));

                byte[] data;
                data = Files.readAllBytes(imgPath);
                requests.add(
                        new AnnotateImageRequest()
                                .setImage(new Image().encodeContent(data))
                                .setFeatures(ImmutableList.of(
                                        new Feature()
                                                .setType("LABEL_DETECTION")
                                                .setMaxResults(maxResults))));
            }
            long startTime = System.currentTimeMillis();
            Vision.Images.Annotate annotate =
                    vision.images()
                            .annotate(new BatchAnnotateImagesRequest().setRequests(requests.build()));
            // Due to a bug: requests to Vision API containing large images fail when GZipped.
            annotate.setDisableGZipContent(true);

            // [START parse_response]
            BatchAnnotateImagesResponse batchResponse = annotate.execute();
            assert batchResponse.getResponses().size() == listFile.listFileNames.size();

            long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Total elapsed http request/response time in milliseconds: " + elapsedTime);
            ImmutableList.Builder<EntityAnnotation> output = ImmutableList.builder();
            for (int i = 0; i < listFile.listFileNames.size(); i++) {
                AnnotateImageResponse response = batchResponse.getResponses().get(i);
                System.out.println(" "+response.getLabelAnnotations());
            }
            return output.build();
        } catch (IOException ex) {
            // Got an exception, which means the whole batch had an error.
            ImmutableList.Builder<EntityAnnotation> output = ImmutableList.builder();

            return output.build();
        }
    }

}

