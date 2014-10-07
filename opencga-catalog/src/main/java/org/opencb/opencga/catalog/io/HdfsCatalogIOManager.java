package org.opencb.opencga.catalog.io;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Created by imedina on 03/10/14.
 */
public class HdfsCatalogIOManager extends CatalogIOManager {


    /**
     * This class implements all the operations for Hadoop HDFS. Useful links:
     *   http://hadoop.apache.org/docs/current/api/org/apache/hadoop/fs/FileSystem.html
     *   http://linuxjunkies.wordpress.com/2011/11/21/a-hdfsclient-for-hadoop-using-the-native-java-api-a-tutorial/
     */

    public void HdfsCatalogIOManager() throws IOException {
        /**
         * Hadoop XML need to be loaded:
         Configuration conf = new Configuration();
         conf.addResource(new Path("/home/hadoop/hadoop/conf/core-site.xml"));
         conf.addResource(new Path("/home/hadoop/hadoop/conf/hdfs-site.xml"));
         conf.addResource(new Path("/home/hadoop/hadoop/conf/mapred-site.xml"));

         FileSystem fileSystem = FileSystem.get(conf);
         */

    }


    public HdfsCatalogIOManager(String propertiesFile) {
        super(propertiesFile);
    }

    public HdfsCatalogIOManager(Properties properties) {
        super(properties);
    }

    @Override
    protected void checkUri(URI param) throws CatalogIOManagerException {

    }

    @Override
    protected void checkDirectoryUri(URI param, boolean writable) throws CatalogIOManagerException {

    }

    @Override
    public boolean exists(URI uri) {
        return false;
    }

    @Override
    public URI createDirectory(URI uri, boolean parents) throws IOException {
        return null;
    }

    @Override
    public void deleteDirectory(URI uri) throws IOException {

    }

    @Override
    protected void deleteFile(URI fileUri) throws IOException {

    }

    @Override
    public void rename(URI oldName, URI newName) throws CatalogIOManagerException, IOException {

    }

    @Override
    public boolean isDirectory(URI uri) {
        return false;
    }

    @Override
    public URI getTmpUri() {
        return null;
    }

    @Override
    public void createFile(String userId, String projectId, String studyId, String filePath, InputStream inputStream) throws CatalogIOManagerException {

    }

    @Override
    public DataInputStream getFileObject(String userid, String projectId, String studyId, String objectId, int start, int limit) throws CatalogIOManagerException, IOException {
        return null;
    }

    @Override
    public DataInputStream getGrepFileObject(String userId, String projectId, String studyId, String objectId, String pattern, boolean ignoreCase, boolean multi) throws CatalogIOManagerException, IOException {
        return null;
    }


}
