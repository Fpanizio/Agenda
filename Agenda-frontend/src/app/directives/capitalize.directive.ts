// src/app/directives/capitalize.directive.ts
import { Directive, HostListener } from '@angular/core';
import { NgControl } from '@angular/forms';

@Directive({
  selector: '[appCapitalize]',
  standalone: false,
})
export class CapitalizeDirective {
  constructor(private ngControl: NgControl) {}

  @HostListener('input', ['$event.target.value'])
  onInput(value: string) {
    if (value) {
      const formatted = this.formatName(value);
      this.ngControl.control?.setValue(formatted, { emitEvent: false });
    }
  }

  private formatName(name: string): string {
    const excludedWords = ['da', 'de', 'do', 'das', 'dos'];

    return name
      .toLowerCase()
      .split(' ')
      .map((word, index) => {
        if (index > 0 && excludedWords.includes(word)) {
          return word;
        }
        return word.charAt(0).toUpperCase() + word.slice(1);
      })
      .join(' ');
  }
}
