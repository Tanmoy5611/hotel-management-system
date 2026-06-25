package be.kdg.prog5.hotels.business.guest;

// Result shown after an admin starts a CSV upload
public record CsvImportResult(boolean inProgress,
                              // Original uploaded file name for the success message
                              String fileName,
                              // Error message when the upload cannot start
                              String errorMessage) {
}