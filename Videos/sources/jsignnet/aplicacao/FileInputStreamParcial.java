package jsignnet.aplicacao;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/aplicacao/FileInputStreamParcial.class */
public class FileInputStreamParcial extends FileInputStream {
    private long _tamanho;
    private long _lidos;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !FileInputStreamParcial.class.desiredAssertionStatus();
    }

    public FileInputStreamParcial(String name, long tamanho) throws FileNotFoundException {
        super(name);
        this._tamanho = tamanho;
    }

    public FileInputStreamParcial(File file, long tamanho) throws FileNotFoundException {
        super(file);
        this._tamanho = tamanho;
    }

    public FileInputStreamParcial(FileDescriptor fdObj, long tamanho) {
        super(fdObj);
        this._tamanho = tamanho;
    }

    @Override // java.io.FileInputStream, java.io.InputStream
    public int read() throws IOException {
        int b = super.read();
        if (b == -1) {
            return b;
        }
        if (this._lidos == this._tamanho) {
            return -1;
        }
        this._lidos++;
        return b;
    }

    @Override // java.io.FileInputStream, java.io.InputStream
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override // java.io.FileInputStream, java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        int cb = super.read(b, off, len);
        if (cb == -1) {
            return cb;
        }
        if (this._lidos == this._tamanho) {
            return -1;
        }
        if (this._lidos + cb <= this._tamanho) {
            this._lidos += cb;
            return cb;
        }
        int dcbLimite = (int) (this._tamanho - this._lidos);
        if (!$assertionsDisabled && dcbLimite >= cb) {
            throw new AssertionError();
        }
        this._lidos = this._tamanho;
        return dcbLimite;
    }
}
