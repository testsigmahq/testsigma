import { Component, OnInit } from '@angular/core';
import {TestsigmaOsConfigServerService} from "../../../services/testsigma-os-config-server.service";

@Component({
  selector: 'app-about',
  host: {'class': 'page-content-container'},
  templateUrl: './about.component.html',
  styles: [
  ]
})
export class AboutComponent implements OnInit {

  serverVersion: string;
  serverIP: string;
  testsigmaLabIP: string[];
  constructor(
    private osServerService: TestsigmaOsConfigServerService
  ) { }

  ngOnInit(): void {
    this.getStorageConfig();
  }
  getStorageConfig()
  {
    this.osServerService.show().subscribe(
      (serverDetails) => {
        this.serverIP = serverDetails.serverIp;
        this.serverVersion = serverDetails.serverVersion;
        this.testsigmaLabIP = serverDetails.testsigmaLabIP;
      });
  }

}
