import {Component, OnInit} from '@angular/core';
import {BaseComponent} from "../../shared/components/base.component";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {AgentService} from "../services/agent.service";
import {AgentDevice} from "../models/agent-device.model";
import {Page} from "../../shared/models/page";
import {Pageable} from "../../shared/models/pageable";
import {DevicesService} from "../services/devices.service";
import {Agent} from "../models/agent.model";
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {MobileRecordingComponent} from "./webcomponents/mobile-recording.component";
import {MirroringData} from "../models/mirroring-data.model";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-agent-devices',
  templateUrl: './devices.component.html',
  styleUrls: []
})

export class DevicesComponent extends BaseComponent implements OnInit {
  public agentId: Number;
  public pageNumber: number = 0;
  public devices: Array<AgentDevice> = [];
  public deviceData: Page<AgentDevice>;
  public devicePageable: Pageable;
  public fetchingCompleted: Boolean = false;
  public device: AgentDevice;
  public agent: Agent;
  public hasMirroredDevice: Boolean;
  public mirroredDevice: AgentDevice;

  public data: MirroringData;
  public dialogRef: MatDialogRef<MobileRecordingComponent>;

  constructor(
    public dialog: MatDialog,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private route: ActivatedRoute,
    private router: Router,
    private devicesService: DevicesService,
    private agentService: AgentService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit() {
    this.data = new MirroringData();
    this.route.params.subscribe((params: Params) => {
      const allParams = {...params, ...{agentId: this.route.parent.params['_value'].agentId}};
      this.pushToParent(this.route, allParams);
      this.agentId = allParams.agentId;
      this.agentService.find(this.agentId).subscribe((response) => {
        this.agent = response;
        this.agentService.ping()
          .subscribe({
            next: (response) => {
              this.agent.isLocalAgent = (response.uniqueId === this.agent.uniqueId);
              this.fetchAgentDevices();
            },
            error: () => {
              this.agent.isLocalAgent = false;
              this.fetchAgentDevices();
            }
          });
      });
    });
  }

  fetchAgentDevices() {
    this.devicesService.findAll(this.agentId).subscribe(response => {
      this.devices = response.content;
      this.deviceData = response;
      this.devicePageable = response.pageable;
      this.fetchingCompleted = true;
    });
  }

  nextPage(): void {
    this.pageNumber += 1;
    this.fetchAgentDevices();
  }

  previousPage(): void {
    this.pageNumber -= 1;
    this.fetchAgentDevices();
  }

  startInspector(device: AgentDevice) {
    this.device = device;
    this.data.agent = this.agent;
    this.data.device = this.device;
    this.dialogRef = this.dialog.open(MobileRecordingComponent, {
      data: this.data,
      disableClose: true,
      panelClass: ['mat-dialog', 'full-width', 'rds-none'],
    });
  }

  stopInspector(device: AgentDevice) {
    this.devicesService.stopMirroring(device)
      .subscribe(() => {
        this.translate.get("mobile_recorder.notification.closed").subscribe((res: string) => {
          this.showNotification(NotificationType.Success, res);
        });
        this.devicesService.show(device).subscribe((_device: AgentDevice) => {
          device.isOnline = _device.isOnline;
          this.hasMirroredDevice = false;
        });
      }, () => {
        this.translate.get("mobile_recorder.notification.closed").subscribe((res: string) => {
          this.showNotification(NotificationType.Error, res);
        });
      });
  }
}

