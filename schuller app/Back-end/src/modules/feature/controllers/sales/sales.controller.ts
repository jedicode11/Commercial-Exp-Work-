import { Controller, HttpCode, Post, UploadedFile, UseInterceptors } from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { of } from 'rxjs';
import { Readable } from 'stream';
import { SalesService } from './../../services/sales/sales.service';

@Controller('sales')
export class SalesController {
  constructor(private salesService: SalesService) {}

  @Post()
  @HttpCode(200)
  async get() {
    return of();
  }

  @Post('import')
  @UseInterceptors(FileInterceptor('file'))
  async uploadSales(@UploadedFile() file: Express.Multer.File) {
    console.log(file);
    const stream = Readable.from(file.buffer.toString());
    return await this.salesService.parseSalesRepresentatives(stream);
  }

  @Post('clear')
  async clearSales() {
    return await this.salesService.clearRepresentatives();
  }

  @Post('import_postcodes')
  @UseInterceptors(FileInterceptor('file'))
  async uploadPostcodes(@UploadedFile() file: Express.Multer.File) {
    console.log(file);
    const stream = Readable.from(file.buffer.toString());
    return await this.salesService.parseSalesPostcodes(stream);
  }

  @Post('clear_postcodes')
  async clearPostcodes() {
    return await this.salesService.clearSalesPostcodes();
  }
}
