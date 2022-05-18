import {Component, Input, OnInit, SimpleChanges} from '@angular/core';
import {PlatformService} from "../../agents/services/platform.service";
import {Platform} from "../../agents/models/platform.model";
import {PlatformOsVersion} from "../../agents/models/platform-os-version.model";
import {FormGroup} from '@angular/forms';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestPlanLabType} from "../../enums/test-plan-lab-type.enum";
import {PlatformBrowser} from "../../agents/models/platform-browser.model";
import {PlatformBrowserVersion} from "../../agents/models/platform-browser-version.model";
import {Agent} from "../../agents/models/agent.model";
import {Browser} from "../../agents/models/browser.model";
import {AgentService} from "../../agents/services/agent.service";
import {Page} from "../../shared/models/page";
import {Pageable} from "../../shared/models/pageable";

@Component({
  selector: 'app-test-plan-platform-os-version-form',
  template: ``,
  styles: []
})
export class TestPlanPlatformOsVersionFormComponent implements OnInit {
  private isBrowserLoaded = false;
  public agentsEmpty: boolean = false;

  constructor(
    public platformService: PlatformService,
    public agentService: AgentService) {
  }

  get agentId() {
    return this.agent?.id;
  }

  get isHybrid() {
    return this.testPlanLabType == TestPlanLabType.Hybrid;
  }

  get isPrivateGrid() {
    return this.testPlanLabType == TestPlanLabType.PrivateGrid;
  }

  @Input('formGroup') environmentFormGroup: FormGroup;
  @Input('version') version: WorkspaceVersion;
  @Input('testPlanLabType') testPlanLabType: TestPlanLabType;
  @Input('isEdit') isEdit?: Boolean;

  public platforms: Platform[];
  public platformOsVersions: PlatformOsVersion[];
  public browsers: PlatformBrowser[];
  public browserVersions: PlatformBrowserVersion[];

  public agents: Page<Agent>;
  public agent: Agent;
  public browser: Browser;
  public platform: Platform;
  public platformOsVersion: PlatformOsVersion;
  public platformBrowser: PlatformBrowser;
  public browserVersion: PlatformBrowserVersion;

  ngOnChanges(changes: SimpleChanges) {
    this.fetchAgents();
    this.agent = null;
    this.browser = null;
    this.platform = null;
    this.platformOsVersion = null;
    this.platformBrowser = null;
    this.browserVersion = null;
    this.isBrowserLoaded = false;
    if (changes && changes['testPlanLabType'] && !this.isEdit) {
      if (!changes['testPlanLabType']['firstChange'] &&
        (changes['testPlanLabType']['currentValue'] == TestPlanLabType.TestsigmaLab)) {
        this.environmentFormGroup?.controls['platformOsVersionId']?.patchValue(null);
      }
    }
    if(changes && changes['environmentFormGroup'] && !changes['environmentFormGroup']['firstChange'] && !this.isEdit && this.isHybrid) {
      this.setTargetMachines();
    }
  }

  ngOnInit(): void {
    this.fetchAgents();
  }

  fetchPlatForms() {
    this.platformService
      .findAll(this.version.workspace.workspaceType, this.testPlanLabType)
      .subscribe(res => {
        this.platforms = res;
        if (this.environmentFormGroup?.controls['platform']?.value) {
          this.platform = this.platforms.find(platform => platform.id == this.environmentFormGroup?.controls['platform'].value);
        }
        if (this.platform==null) {
          this.platform = this.platforms[0];
          this.environmentFormGroup?.controls['platform']?.setValue(this.platform?.id);
        }
        this.fetchOsVersions();
      });
  }

  setPlatform(platformId) {
    this.platform = this.platforms.find(platform => platform.id == platformId);
    this.fetchOsVersions(true);
  }

  fetchOsVersions(setValue?: Boolean) {
    this.platformService.findAllOsVersions(this.platform, this.version.workspace.workspaceType, this.testPlanLabType).subscribe(res => {
      this.platformOsVersions = res;
      if (this.environmentFormGroup?.controls['platformOsVersionId']?.value)
        this.platformOsVersion = this.platformOsVersions.find(osVersion => osVersion.id ==  this.environmentFormGroup?.controls['platformOsVersionId']?.value)
      if (!this.platformOsVersion || setValue) {
        if (this.platformOsVersions.length>0) {
          this.platformOsVersion = this.platformOsVersions[0];
          this.environmentFormGroup?.controls['platformOsVersionId'].setValue(this.platformOsVersion.id);
          this.environmentFormGroup.controls['osVersion']?.setValue(this.platformOsVersion.version);
        }
        else{
          this.platformOsVersion = null;
          this.environmentFormGroup?.controls['platformOsVersionId'].setValue(null);
          this.environmentFormGroup.controls['osVersion']?.setValue("Not Available");
          this.environmentFormGroup.controls['osVersion']?.disable();
        }
      }
      this.postOsVersionFetch(setValue)
    });
  }

  setOsVersion(versionId) {
    this.platformOsVersion = this.platformOsVersions.find(osVersion => osVersion.id == versionId);
    this.environmentFormGroup.controls['osVersion']?.setValue(this.platformOsVersion.version);
    this.fetchBrowsers(true);
  }

  postOsVersionFetch(setValue?: Boolean) {
    if (this.version.workspace.isWeb || this.version.workspace.isMobileWeb)
      this.fetchBrowsers(setValue);
  }

  fetchBrowsers(setValue?: Boolean) {
    this.platformService.findAllBrowsers(this.platform, this.platformOsVersion, this.version.workspace.workspaceType, this.testPlanLabType).subscribe(res => {
      this.browsers = res;
      if (!this.isPrivateGrid) {
        if (this.environmentFormGroup?.controls['browser']?.value)
          this.platformBrowser = this.browsers.find(browser => browser.id == this.environmentFormGroup?.controls['browser']?.value);
        if (!this.platformBrowser || setValue) {
          this.platformBrowser = this.browsers[0];
          this.environmentFormGroup?.controls['browser']?.setValue(this.platformBrowser?.id);
        }
      }
      if (this.isPrivateGrid){
        if (this.environmentFormGroup?.controls['browser']?.value)
          this.platformBrowser = this.browsers.find(browser => browser.name == this.environmentFormGroup?.controls['browser']?.value);
        if (!this.platformBrowser || setValue) {
          this.platformBrowser = this.browsers[0];
          this.environmentFormGroup?.controls['browser']?.setValue(this.platformBrowser?.name);
        }
      }
      if (this.platformBrowser)
        this.fetchBrowserVersions(setValue);
    })
  }

  fetchBrowserVersions(setValue?: Boolean) {
    this.platformService.findAllBrowserVersions(this.platform, this.platformOsVersion, this.platformBrowser, this.version.workspace.workspaceType, this.testPlanLabType).subscribe(res => {
      this.browserVersions = res;
      if ( this.environmentFormGroup?.controls['platformBrowserVersionId']?.value) {
        this.browserVersion = this.browserVersions.find(browserVersion => browserVersion.id === this.environmentFormGroup?.controls['platformBrowserVersionId']?.value)
      }
      if (setValue || !this.browserVersion) {
        if (this.browserVersions.length>0) {
          this.browserVersion = this.browserVersions[0];
          this.environmentFormGroup?.controls['platformBrowserVersionId'].setValue(this.browserVersion.id);
          this.environmentFormGroup?.controls['browserVersion']?.setValue(this.browserVersion?.version);
        }
        else{
          this.browserVersion = null;
          this.environmentFormGroup?.controls['platformBrowserVersionId'].setValue(null);
          this.environmentFormGroup?.controls['browserVersion']?.setValue("Not Available");
          this.environmentFormGroup?.controls['browserVersion']?.disable();
        }
      }
    });
    this.isBrowserLoaded=true;
  }

  setPlatformBrowserVersion(browserVersionId) {
    if (browserVersionId != null) {
      this.browserVersion = this.browserVersions.find(browserVersion => browserVersion.id === browserVersionId)
      this.environmentFormGroup.controls['browserVersion']?.setValue(this.browserVersion?.version);
    }
  }

  setTargetMachines() {

    this.agentService.findAll(null, "updatedDate,desc").subscribe(res => {
      let currentAgent = res?.content.find(agent => this.environmentFormGroup.controls['agentId'].value == agent.id)
      if (currentAgent) {
        this.setAgent(currentAgent, true);
      } else if (res?.content[0]) {
        this.setAgent(res?.content[0], false);
      }
    })
  }

  setAgent(agent: Agent, isEdit?: boolean) {
    this.agent = agent;
    let rawData = this.environmentFormGroup['controls'];
    if (rawData.browser?.value && (isEdit == undefined || isEdit == null))
      isEdit = true;
    let data = {};
    if (!isEdit && this.agent)
      this.browser = this.agent.browsers[0];
    data['platform'] = this.agent?.osType
    data['osVersion'] = this.agent?.osVersion
    data['browser'] = this.browser?.name?.toUpperCase()
    data['browserVersion'] = this.browser?.majorVersion
    if (isEdit && this.agent) {
      let findBrowser = this.agent.browsers.find(browser => browser.name.toUpperCase() == rawData.browser.value);
      let browser: Browser = findBrowser ? findBrowser : this.agent.browsers[0];
      data['platform'] = this.agent?.osType == rawData.platform.value ? rawData.platform.value : this.agent?.osType;
      data['osVersion'] = this.agent?.osVersion == rawData.osVersion.value ? rawData.osVersion.value : this.agent?.osVersion;
      data['browser'] = browser?.name?.toUpperCase()
      data['browserVersion'] = browser?.majorVersion
      this.agent.browsers.filter(browser => {
        if (data['browser'] == browser.name.toUpperCase() || data['browser'].includes(browser.name.toUpperCase())) {
          this.browser = browser;
          data['browser'] = this.browser?.name?.toUpperCase();
        }
      });
      this.agentsEmpty = true;
      setTimeout(()=> {this.agentsEmpty=false}, 10);
    }
    this.environmentFormGroup.controls['platform'].setValue(data['platform'])
    this.environmentFormGroup.controls['osVersion'].setValue(data['osVersion']);
    this.environmentFormGroup.controls['browser']?.setValue(data['browser']);
    this.environmentFormGroup.controls['browserVersion']?.setValue(data['browserVersion']);
  }

  setBrowser(browserName) {
    this.browser = this.agent.browsers?.find(browser => browser.name.toUpperCase() == browserName);
    this.browserVersion = null;
    this.platformBrowser = null;
    this.environmentFormGroup.controls['browserVersion']?.setValue(this.browser?.majorVersion);
    if(!this.isHybrid)
      this.environmentFormGroup.controls['platformBrowserVersionId']?.setValue(this.browser?.id);
  }

  setPlatformBrowser(browserId) {
    this.platformBrowser = this.browsers.find(browser => browser.id == browserId);
    this.fetchBrowserVersions(true);
  }

  setPrivateGridPlatformBrowser(browserName) {
    this.platformBrowser = this.browsers.find(browser => browser.name == browserName);
    this.fetchBrowserVersions(true);
  }

  setAgents(list) {
    this.agents = list;
  }

  setIsEnabled(formControlName) {
    formControlName?.enable();
    return true;
  }

  setIsDisabled(formControlName) {
    formControlName?.disable();
    return true;
  }

  fetchAgents() {
    let pageable = new Pageable();
    pageable.pageSize = 1;
    this.agentService.findAll(null, null,pageable).subscribe(res => this.agentsEmpty = res.empty);
  }

}
