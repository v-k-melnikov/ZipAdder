
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class ZipAdder {

    /**
     * Reads the zip file specified as a second parameter using ZipInputStream,
     * checks the existing files with name like a name of our file specified as a first parameter.
     * Then if this file(s) exists in a archive, replaces content of this file(s) by content
     * of our file. If file doesn't exists, puts file to the base directory.
     *
     * @param filepath The file to be added
     * @param zipFile  The archive to be modified
     * @throws IOException stream exceptions
     */
    public void add(String filepath, String zipFile) throws IOException {

        ZipEntry entry;

        boolean fileExist = false;
        ZipInputStream zipInput = new ZipInputStream(new FileInputStream(zipFile));
        Map<ZipEntry, ArrayList<Byte>> map = new HashMap<>();
        String nextFileName;
        File fileToAdd = new File(filepath);
        List<String> listOfPaths = new ArrayList<>();
        while ((entry = zipInput.getNextEntry()) != null) {
            // compares the name of next file in archive with the name of our file
            try {
                nextFileName = entry.getName().substring(entry.getName().lastIndexOf("/") + 1);
            } catch (StringIndexOutOfBoundsException ex) {
                nextFileName = entry.getName(); // if file doesn't place in directory
            }
            if (nextFileName.equals(fileToAdd.getName())) {
                String fileOutput = entry.getName();   // remembers the path of the file
                listOfPaths.add(fileOutput);
                fileExist = true;
                continue;         // ignores the file with name like our file
            }
            // maps zip file
            ArrayList<Byte> content = new ArrayList<>();
            while (zipInput.available() > 0) {
                content.add((byte) zipInput.read());
            }
            map.put(entry, content);
        }
        zipInput.close();

        // adds our file to archive

        ZipOutputStream zipOutput = new ZipOutputStream(new FileOutputStream(zipFile));
        if (fileExist) {
            for (String path : listOfPaths) {
                zipOutput.putNextEntry(new ZipEntry(path));
                Files.copy(fileToAdd.toPath(), zipOutput);
            }
        } else {
            zipOutput.putNextEntry(new ZipEntry(fileToAdd.getName()));
            Files.copy(fileToAdd.toPath(), zipOutput);
        }
        // writes archive from a map
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




