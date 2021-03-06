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

package org.opencb.opencga.core.models;

import org.opencb.opencga.core.common.TimeUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by pfurio on 02/05/17.
 */
public class Family extends Annotable {

    private long id;
    private String name;

    private List<OntologyTerm> diseases;
    private List<Individual> members;

    private String creationDate;
    private Status status;
    private String description;

    private int release;
    private Map<String, Object> attributes;

    public Family() {
    }

    public Family(String name, List<OntologyTerm> diseases, List<Individual> members, String description, List<AnnotationSet> annotationSets,
                  Map<String, Object> attributes) {
        this(name, diseases, members, TimeUtils.getTime(), new Status(Status.READY), description, -1, annotationSets, attributes);
    }

    public Family(String name, List<OntologyTerm> diseases, List<Individual> members, String creationDate, Status status, String description,
                  int release, List<AnnotationSet> annotationSets, Map<String, Object> attributes) {
        this.name = name;
        this.diseases = defaultObject(diseases, Collections::emptyList);
        this.members = defaultObject(members, Collections::emptyList);
        this.creationDate = defaultObject(creationDate, TimeUtils::getTime);
        this.status = defaultObject(status, new Status());
        this.description = description;
        this.release = release;
        this.annotationSets = defaultObject(annotationSets, Collections::emptyList);
        this.attributes = defaultObject(attributes, Collections::emptyMap);
    }

    public long getId() {
        return id;
    }

    public Family setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Family setName(String name) {
        this.name = name;
        return this;
    }

    public List<OntologyTerm> getDiseases() {
        return diseases;
    }

    public Family setDiseases(List<OntologyTerm> diseases) {
        this.diseases = diseases;
        return this;
    }

    public List<Individual> getMembers() {
        return members;
    }

    public Family setMembers(List<Individual> members) {
        this.members = members;
        return this;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public Family setCreationDate(String creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public Family setStatus(Status status) {
        this.status = status;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Family setDescription(String description) {
        this.description = description;
        return this;
    }

    public int getRelease() {
        return release;
    }

    public Family setRelease(int release) {
        this.release = release;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Family setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }

    public static <O> O defaultObject(O object, O defaultObject) {
        if (object == null) {
            object = defaultObject;
        }
        return object;
    }

    public static <O> O defaultObject(O object, Supplier<O> supplier) {
        if (object == null) {
            object = supplier.get();
        }
        return object;
    }
}
