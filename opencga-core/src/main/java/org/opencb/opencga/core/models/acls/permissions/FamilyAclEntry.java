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

package org.opencb.opencga.core.models.acls.permissions;

import org.opencb.commons.datastore.core.ObjectMap;
import org.opencb.commons.utils.CollectionUtils;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pfurio on 02/05/17.
 */
public class FamilyAclEntry extends AbstractAclEntry<FamilyAclEntry.FamilyPermissions> {

    public enum FamilyPermissions {
        VIEW,
        UPDATE,
        DELETE,
        SHARE,
        WRITE_ANNOTATIONS,
        VIEW_ANNOTATIONS,
        DELETE_ANNOTATIONS
    }

    public FamilyAclEntry() {
        this("", Collections.emptyList());
    }

    public FamilyAclEntry(String member, EnumSet<FamilyPermissions> permissions) {
        super(member, permissions);
    }

    public FamilyAclEntry(String member, ObjectMap permissions) {
        super(member, EnumSet.noneOf(FamilyPermissions.class));

        EnumSet<FamilyPermissions> aux = EnumSet.allOf(FamilyPermissions.class);
        for (FamilyPermissions permission : aux) {
            if (permissions.containsKey(permission.name()) && permissions.getBoolean(permission.name())) {
                this.permissions.add(permission);
            }
        }
    }

    public FamilyAclEntry(String member, List<String> permissions) {
        super(member, EnumSet.noneOf(FamilyPermissions.class));
        if (CollectionUtils.isNotEmpty(permissions)) {
            this.permissions.addAll(permissions.stream().map(FamilyPermissions::valueOf).collect(Collectors.toList()));
        }
    }
}
