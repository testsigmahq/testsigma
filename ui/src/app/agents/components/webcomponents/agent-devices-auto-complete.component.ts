import {Component, OnInit, Input, EventEmitter, Output} from '@angular/core';
import {DevicesService} from "../../services/devices.service";
import {FormControl, FormGroup} from '@angular/forms';
import {Page} from "../../../shared/models/page";
import {AgentDevice} from "../../models/agent-device.model";
import {MobileOsType} from "../../enums/mobile-os-type.enum";
import {TestPlanLabType} from '../../../enums/test-plan-lab-type.enum';

@Component({
  selector: 'app-agent-devices-auto-complete',
  template: `
    <div
      class="w-100">
      <app-auto-complete
        *ngIf="devices?.content"
        [items]="devices"
        [formGroup]="deviceForm"
        [formCtrlName]="formControl"
        [value]="device"
        (onSearch)="fetchDevices($event)"
        (onValueChange)="setDevice($event)"
      ></app-auto-complete>
      <label class="control-label required" [translate]="labelText"></label>
    </div>
  `,
  styles: []
})
export class AgentDevicesAutoCompleteComponent implements OnInit {
  @Input('formGroup') deviceForm: FormGroup;
  @Input('formCtrl') formControl: FormControl;
  @Input('agentId') agentId: number;
  @Input('testPlanLabType') testPlanLabType?: TestPlanLabType;
  @Input('mobileOsType') mobileOsType?: MobileOsType;
  @Input('labelText') labelText: string;
  @Output('onAgentDeviceChange') onAgentDeviceChange = new EventEmitter<AgentDevice>();
  @Output('onDeviceList') onDeviceList = new EventEmitter<Page<AgentDevice>>();

  public devices: Page<AgentDevice>;
  public device: AgentDevice;
  public provisionedAgents: any;
  public query: any;

  constructor(private devicesService: DevicesService) {
  }

  ngOnInit(): void {
  }

  ngOnChanges(): void {
    this.fetchDevices();
  }

  fetchDevices(term?: string) {
    const checkForProvision = this.mobileOsType == 'IOS' && this.testPlanLabType == 'Hybrid';
    if(this.agentId)
      this.devicesService.findAll(this.agentId, undefined, checkForProvision).subscribe(v => {
        this.devices = v;
        if(term!= undefined){
          this.devices.content =  this.devices.content.filter(device => device.name.toLowerCase().includes(term.trim().toLowerCase()))
        }
        if(this.mobileOsType) {
          let filtered = this.devices.content.filter(device => this.mobileOsType == device.osName)
          this.devices.content = [...filtered];
          this.devices.content.forEach( device => {
            if(!device['isOnline']){
              device['isDisabled'] = true;
            } else if (checkForProvision == true && device['provisioned']) {
              device['isDisabled'] = false;
            } else if(checkForProvision == true && !device['provisioned']) {
              device['isDisabled'] = true;
            } else if(this.device == null && this.formControl.value) {
              if(device.id == this.formControl.value) {
                this.device = device;
                this.setDevice(this.device);
              }
            } else if(this.device == null) {
              this.device = device;
              this.setDevice(this.device)
            }
          });
        } else {
          //mobile web
          if(!this.device) {
            this.devices.content.forEach(device => {
              if (device.isOnline) {
                this.device = this.device? this.device : device ;
                device['isDisabled'] = false;
              }else{
                device['isDisabled'] = true;
              }
            })
            if (this.device) {
              if (this.formControl.value)
                this.device = this.devices.content.find((dev) => dev.id == this.formControl.value?.id || dev.id == this.formControl.value)
              this.setDevice(this.device)
            }
          }
        }
        this.onDeviceList.emit(this.devices)
      });
  }

  setDevice(device){
    this.device = device;
    this.formControl.setValue(device);
    this.onAgentDeviceChange.emit(device);
  }

}
