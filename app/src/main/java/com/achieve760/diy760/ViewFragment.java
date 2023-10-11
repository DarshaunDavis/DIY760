package com.achieve760.diy760;

// ... other imports
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.text.TextPaint;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewFragment extends Fragment {

    private static final int FILES_PER_PAGE = 3;

    private ListView listView;
    private TextView fileContent;
    private Button prevButton;
    private Button nextButton;
    private Button printButton;  // Add this line
    private PrintedPdfDocument pdfDocument;
    private SparseIntArray writtenPagesArray;
    private ArrayAdapter<String> adapter;
    private File[] allFiles;
    private String[] filenames;
    private File directory;
    private int currentPage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view, container, false);

        listView = view.findViewById(R.id.listView);
        fileContent = view.findViewById(R.id.fileContent);
        prevButton = view.findViewById(R.id.prevButton);
        nextButton = view.findViewById(R.id.nextButton);
        printButton = view.findViewById(R.id.printButton);  // Add this line
        printButton.setVisibility(View.GONE);  // Hide the button initially

        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPrint(fileContent.getText().toString());
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage > 0) {
                    currentPage--;
                    updateFileList();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((currentPage + 1) * FILES_PER_PAGE < allFiles.length) {
                    currentPage++;
                    updateFileList();
                }
            }
        });

        directory = requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);  // Initialize it here

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        File directory = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        allFiles = directory.listFiles();
        currentPage = 0;
        updateFileList();
        updateListView();
        printButton.setVisibility(View.GONE);  // Hide the button when no file is selected
    }

    private void updateListView() {
        File[] files = directory.listFiles();
        filenames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            filenames[i] = files[i].getName();
        }

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, filenames);
        listView.setAdapter(adapter);
    }

    private void updateFileList() {
        int start = currentPage * FILES_PER_PAGE;
        int end = Math.min(start + FILES_PER_PAGE, allFiles.length);
        String[] filenames = new String[end - start];
        for (int i = 0; i < end - start; i++) {
            filenames[i] = allFiles[start + i].getName();
        }

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, filenames);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Show dialog to confirm deletion
                new AlertDialog.Builder(requireActivity())
                        .setTitle("Delete file")
                        .setMessage("Are you sure you want to delete this file?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked OK
                                File file = new File(directory, allFiles[start + position].getName()); // Use allFiles here
                                boolean deleted = file.delete();
                                if (deleted) {
                                    // Refresh ListView
                                    onResume(); // Since onResume() already has the necessary code to update the file list
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = new File(directory, allFiles[start + position].getName());
                try (FileInputStream fis = new FileInputStream(file)) {
                    InputStreamReader isr = new InputStreamReader(fis);
                    char[] inputBuffer = new char[(int) file.length()];
                    isr.read(inputBuffer);
                    String content = new String(inputBuffer);
                    fileContent.setText(content);
                    printButton.setVisibility(View.VISIBLE);  // Hide the button when no file is selected
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        prevButton.setEnabled(currentPage > 0);
        nextButton.setEnabled((currentPage + 1) * FILES_PER_PAGE < allFiles.length);
    }

    private void doPrint(String letterContent) {
        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) getActivity().getSystemService(Context.PRINT_SERVICE);

        // Set job name, which will be displayed in the print queue
        String jobName = getString(R.string.app_name) + " Document";

        // Start a print job, passing in a PrintDocumentAdapter implementation
        // to handle the generation of a print document
        printManager.print(jobName, new MyPrintDocumentAdapter(getActivity(), letterContent), null);
    }

    private class MyPrintDocumentAdapter extends PrintDocumentAdapter {

        Context context;
        private final String letterContent; // Add this line
        private int totalPages;
        private PrintAttributes currentAttributes;

        public MyPrintDocumentAdapter(Context context, String letterContent) {
            this.context = context;
            this.letterContent = letterContent; // Add this line
        }

        private PageRange[] computeWrittenPages(int pageCount) {
            List<PageRange> writtenPages = new ArrayList<>();
            int startPage = -1;
            int endPage = -1;
            for (int i = 0; i < pageCount; i++) {
                if (writtenPagesArray.get(i, 0) == 1 && startPage < 0) {
                    startPage = i;
                } else if (writtenPagesArray.get(i, 0) == 0 && startPage >= 0) {
                    endPage = i - 1;
                    writtenPages.add(new PageRange(startPage, endPage));
                    startPage = -1;
                    endPage = -1;
                }
            }
            if (startPage >= 0) {
                endPage = pageCount - 1;
                writtenPages.add(new PageRange(startPage, endPage));
            }
            PageRange[] writtenPagesArray = new PageRange[writtenPages.size()];
            writtenPages.toArray(writtenPagesArray);
            return writtenPagesArray;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle metadata) {
            // Calculate the total number of pages based on the content and print attributes
            totalPages = computePageCount(newAttributes);

            // Check for cancellation
            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            // Save the print attributes for use in the onWrite() method
            currentAttributes = newAttributes;

            // Create a PrintDocumentInfo object to report the document information to the print system
            String jobName = context.getString(R.string.app_name) + " Document";
            PrintDocumentInfo info = new PrintDocumentInfo.Builder(jobName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(totalPages)
                    .build();

            // Inform the print system of the document layout
            callback.onLayoutFinished(info, true);
        }

        private int computePageCount(PrintAttributes printAttributes) {
            int itemsPerPage = 40; // default item count for portrait mode

            PrintAttributes.MediaSize pageSize = printAttributes.getMediaSize();
            if (!pageSize.isPortrait()) {
                // Adjust items per page if needed for landscape orientation
                itemsPerPage = 60;
            }

            // Split the letterContent into lines
            String[] lines = letterContent.split("\n");

            // Determine number of print items
            int printItemCount = lines.length;

            return (int) Math.ceil(printItemCount / (double) itemsPerPage);
        }

        @Override
        public void onWrite(final PageRange[] pageRanges,
                            final ParcelFileDescriptor destination,
                            final CancellationSignal cancellationSignal,
                            final WriteResultCallback callback) {
            // Calculate the total number of pages
            int totalPages = computePageCount(currentAttributes);

            // Initialize the written pages array
            writtenPagesArray = new SparseIntArray();

            // Check if the PDF document has been created
            if (pdfDocument == null) {
                // PDF document has not been created - create a new one
                pdfDocument = new PrintedPdfDocument(context, currentAttributes);
            }

            try {
                // Loop over all the pages
                for (int pageIndex = 0; pageIndex < totalPages; pageIndex++) {
                    // Create a new PageInfo object for the current page
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pageIndex).create();

                    // Start a new page using the PageInfo object
                    PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                    // Check for cancellation
                    if (cancellationSignal.isCanceled()) {
                        callback.onWriteCancelled();
                        pdfDocument.close();
                        pdfDocument = null;
                        return;
                    }

                    // Draw the content for the current page
                    drawPage(page, letterContent, pageIndex);

                    // Finish the current page
                    pdfDocument.finishPage(page);

                    // Update the written pages array
                    writtenPagesArray.put(pageIndex, 1);
                }

                // Create an array of PageRange objects representing the written pages
                PageRange[] writtenPages = computeWrittenPages(totalPages);

                // Write PDF document to file
                pdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));

                // Signal the print framework that the document is complete
                callback.onWriteFinished(writtenPages);
            } catch (IOException e) {
                // An error occurred while writing the PDF document
                callback.onWriteFailed(e.toString());
            } finally {
                // Close the PDF document
                pdfDocument.close();
                pdfDocument = null;
            }
        }

        /*private void drawPage(PdfDocument.Page page, String letterContent, int pageIndex) {
            Canvas canvas = page.getCanvas();

            int linesPerPage = 50; // This should match the value in computePageCount()
            String[] lines = letterContent.split("\n");
            int totalPages = (int) Math.ceil((double) lines.length / (double) linesPerPage);

            TextPaint textPaint = new TextPaint();
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(12);

            float marginLeft = 54;
            float marginTop = 72;
            float lineHeight = textPaint.getTextSize() * 1.5f;

            int lineStart = pageIndex * linesPerPage;
            int lineEnd = Math.min(lineStart + linesPerPage, lines.length);

            for (int i = lineStart; i < lineEnd; i++) {
                float x = marginLeft;
                float y = marginTop + ((i - lineStart) * lineHeight);
                canvas.drawText(lines[i], x, y, textPaint);
            }
        }*/
        private void drawPage(PdfDocument.Page page, String letterContent, int pageIndex) {
            Canvas canvas = page.getCanvas();

            int linesPerPage = 50; // This should match the value in computePageCount()
            String[] lines = letterContent.split("\n");

            TextPaint textPaint = new TextPaint();
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(12);

            float marginLeft = 54;
            float marginTop = 72;
            float lineHeight = textPaint.getTextSize() * 1.5f;
            float maxWidth = page.getCanvas().getWidth() - 2 * marginLeft;

            int lineCount = 0;
            for (int i = pageIndex * linesPerPage; i < lines.length && lineCount < linesPerPage; i++) {
                List<String> wrappedLines = wrapText(lines[i], maxWidth, textPaint);
                for (String wrappedLine : wrappedLines) {
                    float x = marginLeft;
                    float y = marginTop + (lineCount * lineHeight);
                    canvas.drawText(wrappedLine, x, y, textPaint);
                    lineCount++;
                }
            }
        }

        private List<String> wrapText(String text, float maxWidth, TextPaint textPaint) {
            List<String> lines = new ArrayList<>();
            if (textPaint.measureText(text) < maxWidth) {
                // If the input string's width is less than maxWidth, it fits as is
                lines.add(text);
            } else {
                // If the input string's width is larger than maxWidth, split it
                String[] words = text.split("\\s");
                String currentLine = words[0];
                for (int i = 1; i < words.length; i++) {
                    if (textPaint.measureText(currentLine + " " + words[i]) <= maxWidth) {
                        currentLine = currentLine + " " + words[i];
                    } else {
                        lines.add(currentLine);
                        currentLine = words[i];
                    }
                }
                if (currentLine.length() > 0) {
                    lines.add(currentLine);
                }
            }
            return lines;
        }

    }
}