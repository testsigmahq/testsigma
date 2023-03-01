import {Component, EventEmitter, Input, OnInit, Output, SimpleChanges} from '@angular/core';
import {PlatformService} from "../../agents/services/platform.service";
import {PlatformScreenResolution} from "../../agents/models/platform-screen-resolution.model";
import {TestPlanPlatformOsVersionFormComponent} from "./test-plan-platform-os-version-form.component";
import {AgentService} from "../../agents/services/agent.service";

@Component({
  selector: 'app-test-plan-test-machine-form',
  templateUrl: './test-plan-test-machine-form.component.html',
  styles: []
})
export class TestPlanTestMachineFormComponent extends TestPlanPlatformOsVersionFormComponent implements OnInit {
  public screenResolutions: PlatformScreenResolution[];
  public screenResolution: PlatformScreenResolution;
  @Input('isAvailableCheck') isAvailableCheck: Boolean;
  @Output() setAgentOnline = new EventEmitter<Boolean>();


  constructor(
    public platformService: PlatformService,
    public agentService: AgentService) {
    super(platformService, agentService);
  }

  ngOnInit(): void {
  }

  ngOnChanges(changes:SimpleChanges) {
    super.ngOnChanges(changes);
    this.screenResolution = null;
    if (this.version && !this.isHybrid) {
      this.environmentFormGroup.patchValue({agentId: undefined});
      this.fetchPlatForms();
    }
    if(this.isHybrid){
      this.setTargetMachines();
    }
  }

  postOsVersionFetch(setValue?: Boolean) {
    super.postOsVersionFetch();
    this.fetchResolutions(setValue);
  }

  fetchResolutions(setValue?: Boolean) {
    this.platformService.findAllScreenResolutions(this.platform, this.platformOsVersion, this.version.workspace.workspaceType, this.testPlanLabType).subscribe(res => {
      this.screenResolutions = res;
      if( this.environmentFormGroup?.controls['platformScreenResolutionId'].value) {
        this.screenResolution = this.screenResolutions.find(res => res.id == this.environmentFormGroup?.controls['platformScreenResolutionId'].value);
      }
      if (setValue || !this.screenResolution) {
        if (this.screenResolutions.length > 0) {
          this.screenResolution = this.screenResolutions[0];
          this.environmentFormGroup?.controls['platformScreenResolutionId'].setValue(this.screenResolution.id);
        } else {
          this.screenResolution = null;
          this.environmentFormGroup?.controls['platformScreenResolutionId'].setValue(null);
        }
      }
      this.setResolution(this.screenResolution);
    })
  }

  setResolution(screenResolution){
    if (screenResolution)
    this.environmentFormGroup?.controls['resolution'].setValue(screenResolution.resolution);
  }

  setAgentStatus(isAgentOnline: boolean){
    this.setAgentOnline.emit(isAgentOnline);
  }
}
