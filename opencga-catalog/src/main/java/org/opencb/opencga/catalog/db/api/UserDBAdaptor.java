/*
 * Copyright 2015-2017 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.opencga.catalog.db.api;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang3.StringUtils;
import org.opencb.commons.datastore.core.*;
import org.opencb.opencga.catalog.exceptions.CatalogDBException;
import org.opencb.opencga.core.models.User;

import java.util.List;
import java.util.Map;

import static org.opencb.commons.datastore.core.QueryParam.Type.*;

/**
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public interface UserDBAdaptor extends DBAdaptor<User> {

    /*
     * User methods
     */
    default boolean exists(String userId) throws CatalogDBException {
        return count(new Query(QueryParams.ID.key(), userId)).getResult().get(0) > 0;
    }

    default void checkId(String userId) throws CatalogDBException {
        if (StringUtils.isEmpty(userId)) {
            throw CatalogDBException.newInstance("User id '{}' is not valid: ", userId);
        }

        if (!exists(userId)) {
            throw CatalogDBException.newInstance("User id '{}' does not exist", userId);
        }
    }

    default void checkIds(List<String> userIds) throws CatalogDBException {
        if (userIds == null || userIds.isEmpty()) {
            throw CatalogDBException.newInstance("No users to be checked.");
        }

        Query query = new Query(QueryParams.ID.key(), userIds);
        if (count(query).first() < userIds.size()) {
            throw CatalogDBException.newInstance("Some users do not exist.");
        }
    }

    QueryResult<User> insert(User user, QueryOptions options) throws CatalogDBException;

    QueryResult<User> get(String userId, QueryOptions options, String lastModified) throws CatalogDBException;

//    @Deprecated
//    default QueryResult<User> modifyUser(String userId, ObjectMap parameters) throws CatalogDBException {
//        return update(userId, parameters);
//    }

    QueryResult<User> update(String userId, ObjectMap parameters) throws CatalogDBException;

//    @Deprecated
//    default QueryResult<User> deleteUser(String userId) throws CatalogDBException {
//        return delete(userId, false);
//    }

    QueryResult<User> delete(String userId, QueryOptions queryOptions) throws CatalogDBException;

    QueryResult changePassword(String userId, String oldPassword, String newPassword) throws CatalogDBException;

    void updateUserLastModified(String userId) throws CatalogDBException;

    QueryResult resetPassword(String userId, String email, String newCryptPass) throws CatalogDBException;

    // Config operations
    QueryResult setConfig(String userId, String name, ObjectMap config) throws CatalogDBException;

    QueryResult<Long> deleteConfig(String userId, String name) throws CatalogDBException;

    // Filter operations
    QueryResult<User.Filter> addFilter(String userId, User.Filter filter) throws CatalogDBException;

    QueryResult<Long> updateFilter(String userId, String name, ObjectMap params) throws CatalogDBException;

    QueryResult deleteFilter(String userId, String name) throws CatalogDBException;

    enum QueryParams implements QueryParam {
        ID("id", TEXT_ARRAY, ""),
        NAME("name", TEXT_ARRAY, ""),
        EMAIL("email", TEXT_ARRAY, ""),
        PASSWORD("password", TEXT_ARRAY, ""),
        ORGANIZATION("organization", TEXT_ARRAY, ""),
        STATUS_NAME("status.name", TEXT, ""),
        STATUS_MSG("status.msg", TEXT, ""),
        STATUS_DATE("status.date", TEXT, ""),
        LAST_MODIFIED("lastModified", TEXT_ARRAY, ""),
        SIZE("size", INTEGER_ARRAY, ""),
        QUOTA("quota", INTEGER_ARRAY, ""),
        ATTRIBUTES("attributes", TEXT, ""), // "Format: <key><operation><stringValue> where <operation> is [<|<=|>|>=|==|!=|~|!~]"
        NATTRIBUTES("nattributes", DECIMAL, ""), // "Format: <key><operation><numericalValue> where <operation> is [<|<=|>|>=|==|!=|~|!~]"
        BATTRIBUTES("battributes", BOOLEAN, ""), // "Format: <key><operation><true|false> where <operation> is [==|!=]"

        PROJECTS("projects", TEXT_ARRAY, ""),
        PROJECT_ID("projects.id", INTEGER_ARRAY, ""),
        PROJECT_NAME("projects.name", TEXT_ARRAY, ""),
        PROJECT_ALIAS("projects.alias", TEXT_ARRAY, ""),
        PROJECT_ORGANIZATION("projects.organization", TEXT_ARRAY, ""),
        PROJECT_STATUS("projects.status", TEXT_ARRAY, ""),
        PROJECT_LAST_MODIFIED("projects.lastModified", TEXT_ARRAY, ""),

        TOOL_ID("tools.id", INTEGER_ARRAY, ""),
        TOOL_NAME("tools.name", TEXT_ARRAY, ""),
        TOOL_ALIAS("tools.alias", TEXT_ARRAY, ""),

        // TOCHECK: Pedro. Check whether login, logout makes sense.
        SESSIONS("sessions", TEXT_ARRAY, ""),
        SESSION_ID("sessions.id", TEXT_ARRAY, ""),
        SESSION_IP("sessions.ip", TEXT_ARRAY, ""),
        SESSION_LOGIN("sessions.login", TEXT_ARRAY, ""),
        SESSION_LOGOUT("sessions.logout", TEXT_ARRAY, ""),

        CONFIGS("configs", TEXT_ARRAY, ""),
        CONFIGS_FILTERS("configs.filters", TEXT_ARRAY, ""),
        CONFIGS_FILTERS_NAME("configs.filters.name", TEXT, "");

        private static Map<String, QueryParams> map;
        static {
            map = new LinkedMap();
            for (QueryParams params : QueryParams.values()) {
                map.put(params.key(), params);
            }
        }

        private final String key;
        private Type type;
        private String description;

        QueryParams(String key, Type type, String description) {
            this.key = key;
            this.type = type;
            this.description = description;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public Type type() {
            return type;
        }

        @Override
        public String description() {
            return description;
        }

        public static Map<String, QueryParams> getMap() {
            return map;
        }

        public static QueryParams getParam(String key) {
            return map.get(key);
        }
    }

    enum FilterParams implements QueryParam {
        NAME("name", TEXT, ""),
        DESCRIPTION("description", TEXT, ""),
        BIOFORMAT("bioformat", TEXT, ""),
        QUERY("query", TEXT, ""),
        OPTIONS("options", TEXT, "");

        private static Map<String, FilterParams> map;

        static {
            map = new LinkedMap();
            for (FilterParams params : FilterParams.values()) {
                map.put(params.key(), params);
            }
        }

        private final String key;
        private Type type;
        private String description;

        FilterParams(String key, Type type, String description) {
            this.key = key;
            this.type = type;
            this.description = description;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public Type type() {
            return type;
        }

        @Override
        public String description() {
            return description;
        }

        public static Map<String, FilterParams> getMap() {
            return map;
        }

        public static FilterParams getParam(String key) {
            return map.get(key);
        }
    }

    enum ToolQueryParams implements QueryParam {
        ID("id", TEXT, ""),
        ALIAS("alias", TEXT, ""),
        NAME("name", TEXT, ""),
        DESCRIPTION("description", TEXT, ""),
        MANIFEST("manifest", TEXT, ""),
        RESULT("result", TEXT, ""),
        PATH("path", TEXT, ""),
        ACL_USER_ID("acl.userId", TEXT_ARRAY, ""),
        ACL_READ("acl.read", BOOLEAN, ""),
        ACL_WRITE("acl.write", BOOLEAN, ""),
        ACL_EXECUTE("acl.execute", BOOLEAN, ""),
        ACL_DELETE("acl.delete", BOOLEAN, "");

        private static Map<String, ToolQueryParams> map;
        static {
            map = new LinkedMap();
            for (ToolQueryParams params : ToolQueryParams.values()) {
                map.put(params.key(), params);
            }
        }

        private final String key;
        private Type type;
        private String description;

        ToolQueryParams(String key, Type type, String description) {
            this.key = key;
            this.type = type;
            this.description = description;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public Type type() {
            return type;
        }

        @Override
        public String description() {
            return description;
        }

        public static Map<String, ToolQueryParams> getMap() {
            return map;
        }

        public static ToolQueryParams getParam(String key) {
            return map.get(key);
        }
    }

    /**
     * Project methods moved to ProjectDBAdaptor
     * ***************************
     */

//    QueryResult<Project> createProject(String userId, Project project, QueryOptions options) throws CatalogDBException;
//
//    boolean projectExists(int projectId);
//
//    QueryResult<Project> getAllProjects(String userId, QueryOptions options) throws CatalogDBException;
//
//    QueryResult<Project> getProject(int project, QueryOptions options) throws CatalogDBException;
//
//    QueryResult<Integer> deleteProject(int projectId) throws CatalogDBException;
//
//    QueryResult renameProjectAlias(int projectId, String newProjectName) throws CatalogDBException;
//
//    QueryResult<Project> modifyProject(int projectId, ObjectMap parameters) throws CatalogDBException;
//
//    int getProjectId(String userId, String projectAlias) throws CatalogDBException;
//
//    String getProjectOwnerId(int projectId) throws CatalogDBException;
//
//    QueryResult<AclEntry> getProjectAcl(int projectId, String userId) throws CatalogDBException;
//
//    QueryResult setProjectAcl(int projectId, AclEntry newAcl) throws CatalogDBException;

}
