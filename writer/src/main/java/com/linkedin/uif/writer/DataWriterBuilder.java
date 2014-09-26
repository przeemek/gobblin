package com.linkedin.uif.writer;

import java.io.IOException;

/**
 * A builder class for {@link DataWriter}.
 *
 * @param <S> schema type
 * @param <D> data record type
 *
 * @author ynli
 */
public abstract class DataWriterBuilder<S, D> {

    protected Destination destination;
    protected String writerId;
    protected WriterOutputFormat format;
    protected S schema;
    protected String filePath;

    /**
     * Tell the writer the destination to write to.
     *
     * @param destination destination to write to
     * @return this {@link DataWriterBuilder} instance
     */
    public DataWriterBuilder<S, D> writeTo(Destination destination) {
        this.destination = destination;
        return this;
    }

    /**
     * Tell the writer the output format of type {@link WriterOutputFormat}
     *
     * @param format output format of the writer
     * @return this {@link DataWriterBuilder} instance
     */
    public DataWriterBuilder<S, D> writeInFormat(WriterOutputFormat format) {
        this.format = format;
        return this;
    }

    /**
     * Give the writer a unique ID.
     *
     * @param writerId unique writer ID
     * @return this {@link DataWriterBuilder} instance
     */
    public DataWriterBuilder<S, D> withWriterId(String writerId) {
        this.writerId = writerId;
        return this;
    }

    /**
     * Tell the writer the data schema.
     *
     * @param schema data schema
     * @return this {@link DataWriterBuilder} instance
     */
    public DataWriterBuilder<S, D> withSchema(S schema) {
        this.schema = schema;
        return this;
    }

    /**
     * Tell the writer the file path for the output file.
     *
     * @param filePath is the path the file will be written to
     * @return this {@link DataWriterBuilder} instance
     */
    public DataWriterBuilder<S, D> withFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }
    
    /**
     * Build a {@link DataWriter}.
     *
     * @throws IOException if there is anything wrong building the writer
     * @return the built {@link DataWriter}
     */
    @SuppressWarnings("unchecked")
    public abstract DataWriter<D> build() throws IOException;
}
