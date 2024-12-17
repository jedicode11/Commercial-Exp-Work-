import { IsOptional, IsString } from 'class-validator';

export class SendDto {
  @IsString()
  @IsOptional()
  readonly location_code: string | undefined;

  @IsString()
  @IsOptional()
  readonly gender: string | undefined;

  @IsString()
  readonly first_name: string;

  @IsString()
  readonly firstName: string;

  @IsString()
  readonly last_name: string;

  @IsString()
  readonly lastName: string;

  @IsString()
  readonly company: string;

  @IsString()
  readonly company_ort: string;

  @IsString()
  readonly country: string;

  @IsString()
  readonly city: string;

  @IsString()
  readonly zip_code: string;

  @IsString()
  readonly sales_id: string;

  @IsString()
  readonly ticket: string;
}
