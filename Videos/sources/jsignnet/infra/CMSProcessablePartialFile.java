package jsignnet.infra;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/infra/CMSProcessablePartialFile.class */
public class CMSProcessablePartialFile implements CMSProcessable {
    private static final int DEFAULT_BUF_SIZE = 32768;
    private final File _file;
    private final long _fileSize;
    private final byte[] _buf;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !CMSProcessablePartialFile.class.desiredAssertionStatus();
    }

    public CMSProcessablePartialFile(File file, long fileSize) {
        this(file, fileSize, DEFAULT_BUF_SIZE);
    }

    public CMSProcessablePartialFile(File file, long fileSize, int bufSize) {
        this._file = file;
        this._buf = new byte[bufSize];
        this._fileSize = fileSize;
    }

    public void write(OutputStream zOut) throws CMSException, IOException {
        int toBeRead;
        FileInputStream fIn = new FileInputStream(this._file);
        long bytesLeft = this._fileSize;
        long jMin = Math.min(bytesLeft, this._buf.length);
        while (true) {
            toBeRead = (int) jMin;
            int len = fIn.read(this._buf, 0, toBeRead);
            if (len <= 0) {
                break;
            }
            zOut.write(this._buf, 0, len);
            bytesLeft -= len;
            jMin = Math.min(bytesLeft, this._buf.length);
        }
        if (!$assertionsDisabled && toBeRead != 0) {
            throw new AssertionError(toBeRead + " bytes weren´t read from the file.");
        }
        fIn.close();
    }

    public Object getContent() {
        return this._file;
    }
}
