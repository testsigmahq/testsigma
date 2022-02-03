import {Component, EventEmitter, OnInit, Output} from '@angular/core';

import $S from 'scriptjs';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {BaseComponent} from "../../shared/components/base.component";
import {UserPreferenceService} from "../../services/user-preference.service";
import {UserPreference} from "../../models/user-preference.model";

@Component({
  selector: 'app-fresh-chat-widget',
  template: `
    <div
      cdkDrag
      cdkDragBoundary=".quick-start-btn-boundary"
      cdkDragLockAxis="x"
      class="chat-icon-right" (click)="open()"
      [matTooltip]="'chat_to_us.tooltip.reposition' | translate" [matTooltipPosition]="'above'" [matTooltipShowDelay]="500">
      <i class="chat-sm-icon">
      </i>
    </div>
  `,
  styles: [``]
})
export class FreshChatWidgetComponent extends BaseComponent implements OnInit {
  @Output('onOpenChat') onOpenChat = new EventEmitter<any>();
  public fcSettings = {
    token: "b905859c-c256-471f-ab0a-a4d0829d27ee",
    host: "https://wchat.freshchat.com",
    siteId: "app.testsigma.com",
    faqTags: {
      tags : ['Websitepricing'],
      filterType:'category'
    },
    config:{
      cssNames:{
        widget:"custom_fc_frame"
      },
      headerProperty:{
        direction: 'rtl',
        hideChatButton: true
      }
    }
  };
  public freshChatWidgetOpened: boolean;
  public hasMessage: boolean;
  public userPreference: UserPreference;

  constructor(
    public userPreferenceService: UserPreferenceService,
    public authGuard: AuthenticationGuard) {
    super(authGuard);
  }

  ngOnInit(): void {
    this.fetchFreshChatRestoreId();
    this.loadFreshChatScript();

  }

  fetchFreshChatRestoreId() {
    this.userPreferenceService.show().subscribe((res: UserPreference) => {
      this.userPreference = res;
      this.loadFreshChatScript();
    })
  }

  loadFreshChatScript() {
    // @ts-ignore
    window.fcSettings = this.fcSettings;
    // @ts-ignore
    window.fcSettings['externalId'] = this.authGuard.session.user.email;
    // @ts-ignore
    window.fcSettings['email'] = this.authGuard.session.user.email;
    // @ts-ignore
    window.fcSettings['firstName'] = this.authGuard.session.user.firstName;
    // @ts-ignore
    window.fcSettings['lastName'] = this.authGuard.session.user.lastName;
    // @ts-ignore
    window.fcSettings.onInit = this.onFreshChatWidgetInit.bind(this);

    $S('https://wchat.freshchat.com/js/widget.js', ()=> {
      // @ts-ignore
      console.log(<any>window.fcWidget);
    })
  }

  onFreshChatWidgetInit() {
    // @ts-ignore
    window.fcWidget.on("widget:loaded", this.onFreshChatOnLoaded.bind(this))
  }

  onFreshChatOnLoaded() {
    // @ts-ignore
    const widget = window.fcWidget;
    widget.on("widget:opened", this.onFreshChatWidgetOpened.bind(this))
    widget.on("widget:closed", this.onFreshChatWidgetClosed.bind(this))
    widget.on("unreadCount:notify", this.onFreshChatNewMessages.bind(this))
    this.setFreshChatUserProperties();
  }

  onFreshChatNewMessages(resp) {
    // @ts-ignore
    this.hasMessage=resp.count && !window.fcWidget.isOpen();
  }

  onFreshChatWidgetOpened() {
    this.freshChatWidgetOpened=true;
    this.hasMessage=false;
  }

  onFreshChatWidgetClosed() {
    setTimeout(()=> this.freshChatWidgetOpened=false, 100);
  }

  setFreshChatUserProperties(){
    // @ts-ignore
    window.fcWidget.user.get((resp)=> {
      this.onFreshChatUserFetch(resp);
    });
  }

  onFreshChatUserFetch(resp) {
    let properties = {
      firstName: this.authGuard.session.user.firstName,
      lastName: this.authGuard.session.user.lastName,
      email: this.authGuard.session.user.email,
      domain: this.authGuard.session.user.domain,
    }
    const status = resp && resp.status;
    if (status !== 200) {
      // @ts-ignore
      window.fcWidget.user.setProperties(properties);
      // @ts-ignore
      window.fcWidget.on('user:created', this.onFreshChatUserCreated.bind(this));
    }
  }

  onFreshChatUserCreated(resp) {
    const status = resp && resp.status,
      data = resp && resp.data;
    if (status === 200) {
      if (data.restoreId) {
        this.userPreferenceService.save(this.userPreference);
      }
    }
  }

  open() {
    this.freshChatWidgetOpened=true;
    // @ts-ignore
    window.fcWidget.open();
    this.onOpenChat.emit();
  }

}
