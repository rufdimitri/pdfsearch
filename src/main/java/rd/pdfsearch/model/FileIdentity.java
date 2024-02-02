package rd.pdfsearch.model;

import java.util.Objects;

/**
 * This Class is used to represent a file version, it can be used to check if file was changed
 * @param filename file name without path
 * @param filepath path where file saved
 * @param size size of the file in bytes
 * @param modifiedDate when the file was modified
 */
public record FileIdentity(String filename, String filepath, long size, long modifiedDate) {

    public FileIdentity(String filename, String filepath, long size, long modifiedDate) {
        this.filename = filename;
        this.filepath = filepath;
        this.size = size;
        this.modifiedDate = modifiedDate / 1000 * 1000; //trim 3 last digits, to erase milliseconds, to avoid problems with different file systems
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileIdentity that = (FileIdentity) o;
        return size == that.size && modifiedDate == that.modifiedDate && Objects.equals(filename, that.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename, size, modifiedDate);
    }
}
