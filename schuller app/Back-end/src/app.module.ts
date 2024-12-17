import { HttpModule } from '@nestjs/axios';
import { Module } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { ServeStaticModule } from '@nestjs/serve-static';
import { TypeOrmModule } from '@nestjs/typeorm';
import { CsvModule } from 'nest-csv-parser';
import { DynamicConfigModule, DynamicConfigSource } from 'nestjs-dynamic-config';
import { join } from 'path';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { FeatureModule } from './modules/feature/feature.module';
import entities from './typeorm';

const onError = (error: Error) => {
  console.log('🚀 ~ file: app.module.ts ~ line 15 ~ onError ~ error.message', error.message);
  // appLogger.error(error.message, error.stack, 'Config');
  process.exit(1);
};

@Module({
  imports: [
    HttpModule,
    ConfigModule.forRoot(),
    ServeStaticModule.forRoot({
      rootPath: join(__dirname, '..', 'public'),
    }),
    CsvModule,
    ConfigModule.forRoot({ isGlobal: true }),
    TypeOrmModule.forRootAsync({
      imports: [ConfigModule],
      useFactory: (configService: ConfigService) => ({
        type: 'postgres',
        host: configService.get('DB_HOST'),
        port: +configService.get<number>('DB_PORT'),
        username: configService.get('DB_USERNAME'),
        password: configService.get('DB_PASSWORD'),
        database: configService.get('DB_NAME'),
        entities: entities,
        autoLoadEntities: true,
        synchronize: true,
      }),
      inject: [ConfigService],
    }),
    DynamicConfigModule.forRoot({
      source: DynamicConfigSource.FILE,
      configFilePath: process.env['CONFIG_FILE'],
      watchForChanges: true,
      onError,
      parseConfig: res => {
        let config = {};
        try {
          config = JSON.parse(res);
        } catch (e) {
          console.error(e.message);
        }
        return config;
      },
    }),
    FeatureModule,
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
