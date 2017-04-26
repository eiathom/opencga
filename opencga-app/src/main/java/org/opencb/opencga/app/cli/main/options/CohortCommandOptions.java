/*
 * Copyright 2015-2016 OpenCB
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

package org.opencb.opencga.app.cli.main.options;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import org.opencb.opencga.app.cli.main.options.commons.AclCommandOptions;
import org.opencb.opencga.app.cli.main.options.commons.AnnotationCommandOptions;
import org.opencb.opencga.catalog.models.Study;

import static org.opencb.opencga.app.cli.GeneralCliOptions.*;

/**
 * Created by sgallego on 6/14/16.
 */
@Parameters(commandNames = {"cohorts"}, commandDescription = "Cohorts commands")
public class CohortCommandOptions {

    public CreateCommandOptions createCommandOptions;
    public InfoCommandOptions infoCommandOptions;
    public SearchCommandOptions searchCommandOptions;
    public SamplesCommandOptions samplesCommandOptions;
    public UpdateCommandOptions updateCommandOptions;
    public DeleteCommandOptions deleteCommandOptions;
    public StatsCommandOptions statsCommandOptions;
    public GroupByCommandOptions groupByCommandOptions;

    public AclCommandOptions.AclsCommandOptions aclsCommandOptions;
    public AclCommandOptions.AclsMemberInfoCommandOptions aclsMemberInfoCommandOptions;
    public AclCommandOptions.AclsUpdateCommandOptions aclsUpdateCommandOptions;

    public AnnotationCommandOptions.AnnotationSetsCreateCommandOptions annotationCreateCommandOptions;
    public AnnotationCommandOptions.AnnotationSetsAllInfoCommandOptions annotationAllInfoCommandOptions;
    public AnnotationCommandOptions.AnnotationSetsSearchCommandOptions annotationSearchCommandOptions;
    public AnnotationCommandOptions.AnnotationSetsDeleteCommandOptions annotationDeleteCommandOptions;
    public AnnotationCommandOptions.AnnotationSetsInfoCommandOptions annotationInfoCommandOptions;
    public AnnotationCommandOptions.AnnotationSetsUpdateCommandOptions annotationUpdateCommandOptions;

    public JCommander jCommander;
    public CommonCommandOptions commonCommandOptions;
    public DataModelOptions commonDataModelOptions;
    public NumericOptions commonNumericOptions;

    private AnnotationCommandOptions annotationCommandOptions;

    public CohortCommandOptions(CommonCommandOptions commonCommandOptions, DataModelOptions dataModelOptions, NumericOptions numericOptions,
                                JCommander jCommander) {

        this.commonCommandOptions = commonCommandOptions;
        this.commonDataModelOptions = dataModelOptions;
        this.commonNumericOptions = numericOptions;
        this.jCommander = jCommander;

        this.createCommandOptions = new CreateCommandOptions();
        this.infoCommandOptions = new InfoCommandOptions();
        this.searchCommandOptions = new SearchCommandOptions();
        this.samplesCommandOptions = new SamplesCommandOptions();
        this.updateCommandOptions = new UpdateCommandOptions();
        this.deleteCommandOptions = new DeleteCommandOptions();
        this.statsCommandOptions = new StatsCommandOptions();
        this.groupByCommandOptions = new GroupByCommandOptions();

        this.annotationCommandOptions = new AnnotationCommandOptions(commonCommandOptions);
        this.annotationCreateCommandOptions = this.annotationCommandOptions.getCreateCommandOptions();
        this.annotationAllInfoCommandOptions = this.annotationCommandOptions.getAllInfoCommandOptions();
        this.annotationSearchCommandOptions = this.annotationCommandOptions.getSearchCommandOptions();
        this.annotationDeleteCommandOptions = this.annotationCommandOptions.getDeleteCommandOptions();
        this.annotationInfoCommandOptions = this.annotationCommandOptions.getInfoCommandOptions();
        this.annotationUpdateCommandOptions = this.annotationCommandOptions.getUpdateCommandOptions();

        AclCommandOptions aclCommandOptions = new AclCommandOptions(commonCommandOptions);
        this.aclsCommandOptions = aclCommandOptions.getAclsCommandOptions();
        this.aclsMemberInfoCommandOptions = aclCommandOptions.getAclsMemberInfoCommandOptions();
        this.aclsUpdateCommandOptions = aclCommandOptions.getAclsUpdateCommandOptions();
    }

    public class BaseCohortsCommand extends StudyOption {

        @ParametersDelegate
        public CommonCommandOptions commonOptions = commonCommandOptions;

        @Parameter(names = {"--cohort"}, description = "Cohort id", required = true, arity = 1)
        public String cohort;

    }

    @Parameters(commandNames = {"create"}, commandDescription = "Create a cohort")
    public class CreateCommandOptions extends StudyOption {

        @ParametersDelegate
        public CommonCommandOptions commonOptions = commonCommandOptions;

        @Parameter(names = {"-n", "--name"}, description = "Cohort name.", required = true, arity = 1)
        public String name;

        @Parameter(names = {"--type"}, description = "Cohort type", required = false, arity = 1)
        public Study.Type type = Study.Type.COLLECTION;

        @Parameter(names = {"--variable-set-id"}, description = "VariableSetId", required = false, arity = 1)
        public String variableSetId;

        @Parameter(names = {"-d", "--description"}, description = "cohort description", required = false, arity = 1)
        public String description;

        @Parameter(names = {"--samples"}, description = "Sample ids for the cohort (CSV)", required = false, arity = 1)
        public String sampleIds;

        @Parameter(names = {"--variable"}, description = "Categorical variable name to use to create cohorts, must go together the "
                + "parameter variable-set-id", required = false, arity = 1)
        public String variable;
    }

    @Parameters(commandNames = {"search"}, commandDescription = "Search cohorts")
    public class SearchCommandOptions extends StudyOption {

        @ParametersDelegate
        public CommonCommandOptions commonOptions = commonCommandOptions;

        @ParametersDelegate
        public DataModelOptions dataModelOptions = commonDataModelOptions;

        @ParametersDelegate
        public NumericOptions numericOptions = commonNumericOptions;

        @Parameter(names = {"--name"}, description = "Comma separated list of file names", required = false, arity = 1)
        public String name;

        @Parameter(names = {"--type"}, description = "Cohort type.", arity = 1)
        public String type;

        @Parameter(names = {"--status"}, description = "Status.", arity = 1)
        public String status;

        @Parameter(names = {"--samples"}, description = "Comma separated list of sample ids", arity = 1)
        public String samples;

    }

    @Parameters(commandNames = {"info"}, commandDescription = "Get cohort information")
    public class InfoCommandOptions extends BaseCohortsCommand {

        @ParametersDelegate
        public DataModelOptions dataModelOptions = commonDataModelOptions;

    }

    @Parameters(commandNames = {"samples"}, commandDescription = "List samples belonging to a cohort")
    public class SamplesCommandOptions extends BaseCohortsCommand {

        @ParametersDelegate
        public DataModelOptions dataModelOptions = commonDataModelOptions;

        @ParametersDelegate
        public NumericOptions numericOptions = commonNumericOptions;

    }

    @Parameters(commandNames = {"stats"}, commandDescription = "Calculate variant stats for a set of cohorts.")
    public class StatsCommandOptions extends BaseCohortsCommand {

        @Parameter(names = {"--calculate"}, description = "Calculate cohort stats", arity = 0)
        public boolean calculate;

        @Parameter(names = {"--delete"}, description = "Delete stats [PENDING]", arity = 0)
        public boolean delete;

        @Parameter(names = {"--log"}, description = "Log level", required = false, arity = 1)
        public String log = "";

        @Parameter(names = {"-o", "--outdir-id"}, description = "Directory ID where to create the file", required = false, arity = 1)
        public String outdirId = "";

    }

    @Parameters(commandNames = {"update"}, commandDescription = "Update cohort")
    public class UpdateCommandOptions extends BaseCohortsCommand {

        @Parameter(names = {"-n", "--name"}, description = "New cohort name.", required = false, arity = 1)
        public String name;

        @Parameter(names = {"--creation-date"}, description = "Creation date", required = false, arity = 1)
        public String creationDate;

        @Parameter(names = {"-d", "--description"}, description = "Description", required = false, arity = 1)
        public String description;

        @Parameter(names = {"--samples"}, description = "Comma separated values of sampleIds. Will replace all existing sampleIds",
                required = false, arity = 0)
        public String samples;
    }

    @Parameters(commandNames = {"delete"}, commandDescription = "Delete cohort")
    public class DeleteCommandOptions extends BaseCohortsCommand {

    }

    @Parameters(commandNames = {"group-by"}, commandDescription = "GroupBy cohort")
    public class GroupByCommandOptions extends StudyOption {

        @ParametersDelegate
        public CommonCommandOptions commonOptions = commonCommandOptions;

        @Parameter(names = {"-f", "--fields"}, description = "Comma separated list of fields by which to group by.", required = true, arity = 1)
        public String fields;

        @Deprecated
        @Parameter(names = {"--id"}, description = "[DEPRECATED] Comma separated list of ids.", required = false, arity = 1)
        public String id;

        @Parameter(names = {"-n", "--name"}, description = "Comma separated list of names.", required = false, arity = 1)
        public String name;

        @Parameter(names = {"--type"}, description = "Comma separated Type values.", required = false, arity = 1)
        public String type;

        @Parameter(names = {"--status"}, description = "Status.", required = false, arity = 1)
        public String status;

        @Parameter(names = {"--creation-date"}, description = "Creation date.", required = false, arity = 1)
        public String creationDate;

        @Parameter(names = {"--sample-ids"}, description = "Sample ids", required = false, arity = 1)
        public String sampleIds;

        @Parameter(names = {"-d", "--description"}, description = "Description", required = false, arity = 1)
        public String description;

        @Parameter(names = {"--attributes"}, description = "Attributes", required = false, arity = 1)
        public String attributes;

        @Parameter(names = {"--nattributes"}, description = "numerical attributes", required = false, arity = 1)
        public String nattributes;
    }

}
