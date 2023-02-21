import {Component, Input, OnInit, SimpleChanges} from '@angular/core';
import {PlatformService} from "../../agents/services/platform.service";
import {CloudDevice} from "../../agents/models/cloud-device.model";
import {Page} from "../../shared/models/page";
import {Pageable} from "../../shared/models/pageable";
import {TestPlanPlatformOsVersionFormComponent} from "./test-plan-platform-os-version-form.component";
import {AgentDevice} from "../../agents/models/agent-device.model";
import {PlatformBrowser} from "../../agents/models/platform-browser.model";
import {MobileOsType} from "../../agents/enums/mobile-os-type.enum";
import {AgentService} from "../../agents/services/agent.service";
import {Browser} from "../../agents/models/browser.model";
import {WebBrowser} from "../../enums/web-browser";

@Component({
  selector: 'app-test-plan-device-form',
  templateUrl: './test-plan-device-form.component.html',
  styles: []
})
export class TestPlanDeviceFormComponent extends TestPlanPlatformOsVersionFormComponent implements OnInit {

  public cloudDevices: CloudDevice[];
  public cloudDevicePage: Page<CloudDevice> = new Page<CloudDevice>();
  public agentDevice: AgentDevice;
  public cloudDevice: CloudDevice;
  public isDeviceListInProgress = false;
  @Input('isAvailableCheck') isAvailableCheck: Boolean;

  public platformDeviceId : Number;

  constructor(
    public platformService: PlatformService,
    public agentService: AgentService) {
    super( platformService, agentService)
  }

  ngOnInit(): void {
  }

  ngOnChanges(changes:SimpleChanges) {
    super.ngOnChanges(changes);
    this.agentDevice = null;
    this.cloudDevice = null;
    if (this.version)
      this.fetchPlatForms();
  }

  postOsVersionFetch(setValue?: Boolean) {
    super.postOsVersionFetch(setValue);
    this.fetchDevices(setValue);
  }

  public fetchDevices(setValue?: Boolean) {
    this.isDeviceListInProgress = true;
    if (this.platform){
    this.platformService.findAllDevices(this.platform, this.platformOsVersion, this.version.workspace.workspaceType, this.testPlanLabType).subscribe(res => {
      this.cloudDevices = res;
      this.cloudDevices.forEach(device => {
        if(device.displayName) {
          device.name = device.displayName;
        }
        device['isDisabled'] = !device.isAvailable;
        if(device.isAvailable)
        device['suffix'] = " (Available)";
      });
      this.cloudDevicePage.content = this.cloudDevices;
      this.cloudDevicePage.pageable = new Pageable();
      this.cloudDevicePage.pageable.pageNumber = 0;
      this.cloudDevicePage.totalPages = 1;
      this.cloudDevicePage.totalElements = this.cloudDevices.length;
      let deviceId = this.environmentFormGroup?.controls['platformDeviceId']?.value;
      if (setValue || !deviceId) {
        this.cloudDevice = this.cloudDevicePage.content.find(device => device.isAvailable);
        this.environmentFormGroup?.controls?.['platformDeviceId']?.setValue(this.cloudDevice?.id);
      } else {
        this.cloudDevice = this.cloudDevices.find(device => device.id == deviceId);
      }
      this.environmentFormGroup?.controls?.['deviceName']?.setValue(this.cloudDevice?.name);
      this.isDeviceListInProgress =false;
      if(this.cloudDevice){
        this.setCloudDevice(this.cloudDevice);
      }
      }
    )};
  }

  setOsVersion(versionId) {
    this.platformOsVersion = this.platformOsVersions.find(osVersion => osVersion.id == versionId);
    this.environmentFormGroup.controls['osVersion'].setValue(this.platformOsVersion.version);
    this.fetchDevices(true);
  }

  fetchCloudDevices(term?: string) {
    this.cloudDevicePage = new Page<CloudDevice>();
    if (!term) {
      this.cloudDevicePage.content = this.cloudDevices;
    } else {
      this.cloudDevicePage.content = this.cloudDevices.filter(device => {
        return device.name.toLowerCase().indexOf(term.trim().toLowerCase()) > -1
      });
    }
  }

  setCloudDevice(cloudDevice) {
    this.environmentFormGroup.controls['platformDeviceId'].setValue(cloudDevice?.id);
    this.environmentFormGroup.controls['deviceName'].setValue(cloudDevice?.name);
    if (this.version.workspace.isMobileWeb) {
      let browser = new PlatformBrowser().deserialize({id: WebBrowser.CHROME, name: WebBrowser.CHROME});
      if (this.platform.isIOS)
        browser = new PlatformBrowser().deserialize({id: WebBrowser.SAFARI, name: WebBrowser.SAFARI});
      this.environmentFormGroup.controls['browser']?.setValue(browser.name.toUpperCase());
    }
  }

  setAgentDevice(agentDevice: AgentDevice) {
    this.agentDevice = null;
    this.agentDevice = agentDevice;
    this.platformOsVersion=this.platformOsVersions.find(osVersion => osVersion.version==agentDevice?.osVersion.split(".")[0]+".0")
    this.environmentFormGroup.controls['platformOsVersionId'].setValue(this.platformOsVersion.id)
    this.environmentFormGroup.controls['deviceId'].setValue(agentDevice?.id);
    if (this.version.workspace.isMobileWeb) {
      let browser = new PlatformBrowser().deserialize({id: "chrome", name: "Chrome"});
      if (agentDevice?.isIOS)
        browser = new PlatformBrowser().deserialize({id: "safari", name: "Safari"});
      this.environmentFormGroup.controls['browser']?.setValue(browser.name.toUpperCase());
    }
    this.environmentFormGroup?.controls['deviceName'].setValue(this.agentDevice?.name);
  }

  get mobileOsType() {
    if(!this.version.workspace.isMobileNative)
      return ;
    return this.version.workspace.isAndroidNative ? MobileOsType.ANDROID : MobileOsType.IOS;
  }
}
