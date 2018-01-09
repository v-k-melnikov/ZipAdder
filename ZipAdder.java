
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * ZipAdder reads the zip file specified as a second parameter using ZipInputStream,
 * checks the existing file with name like a name of our file specified as a first parameter.
 * Then if this file exists in a archive, ZipAdder replaces content of this file by content
 * of our file. If file doesn't exists, ZipAdder creates a directory 'new' and puts file there.
 **/

public class ZipAdder {

    public static void main(String[] args) throws IOException {

        ZipEntry entry;
        String fileOutput = "";

        // Maps the zip file passed as argument 2
        String zipArchiveAbsolutePath = args[1];
        boolean fileExist = false;
        ZipInputStream zipInput = new ZipInputStream(new FileInputStream(zipArchiveAbsolutePath));
        Map<ZipEntry, ArrayList<Byte>> map = new HashMap<>();
        String nextFileName;
        File fileToAdd = new File(args[0]);
        while ((entry = zipInput.getNextEntry()) != null) {
            // Gets the name of next file in archive
            try {
                nextFileName = entry.getName().substring(entry.getName().lastIndexOf("/") + 1);
            } catch (StringIndexOutOfBoundsException ex) {
                nextFileName = entry.getName(); // If file doesn't places in directory
            }
            if (nextFileName.equals(fileToAdd.getName())) {
                fileOutput = entry.getName();   // Remembers the path of file
                fileExist = true;
                continue;         // Ignores the file with name like our file
            }
            ArrayList<Byte> arr = new ArrayList<>();
            while (zipInput.available() > 0) {
                arr.add((byte) zipInput.read());
            }
            map.put(entry, arr);
        }
        zipInput.close();
        // Adds our file to archive

        ZipOutputStream zipOutput = new ZipOutputStream(new FileOutputStream(zipArchiveAbsolutePath));
        if (fileExist) {
            zipOutput.putNextEntry(new ZipEntry(fileOutput));
            Files.copy(fileToAdd.toPath(), zipOutput);
        } else {
            zipOutput.putNextEntry(new ZipEntry("new/" + fileToAdd.getName()));
            Files.copy(fileToAdd.toPath(), zipOutput);
        }
        // Writes archive from map
        for (Map.Entry<ZipEntry, ArrayList<Byte>> l : map.entrySet()) {
            byte[] ar = new byte[l.getValue().size()];
            for (int i = 0; i < l.getValue().size() - 1; i++) {
                ar[i] = l.getValue().get(i);
            }
            zipOutput.putNextEntry(new ZipEntry(l.getKey().getName()));
            zipOutput.write(ar);
            zipOutput.flush();
        }
        zipOutput.close();
    }
}




