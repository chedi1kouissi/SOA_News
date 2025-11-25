import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-article-card',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './article-card.component.html',
    styleUrl: './article-card.component.css'
})
export class ArticleCardComponent {
    @Input() article: any;
    @Input() isSaved: boolean = false;
    @Input() showSaveButton: boolean = true;
    @Output() save = new EventEmitter<any>();
    @Output() unsave = new EventEmitter<any>();

    onSave() {
        this.save.emit(this.article);
    }

    onUnsave() {
        this.unsave.emit(this.article);
    }
}
