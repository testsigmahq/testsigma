import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-version-help',
  template: `
    <div>
      <i class="fa-help"></i>
      <span class="pl-10" [translate]="'hint.message.common.help'"></span>
      <div class="mt-20 pb-10">
        <span class="rb-medium pr-2" [translate]="'project.applications.new_version.help.note'"></span>
        <span [translate]="'project.applications.new_version.help.note.description'"></span>
      </div>
      <div class="mt-20 pb-10 rb-medium" [translate]="'project.applications.new_version.help.description'"></div>
      <span [translate]="'project.applications.new_version.help.description.content'"></span>
    </div>
  `,
  styles: []
})
export class VersionHelpComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
