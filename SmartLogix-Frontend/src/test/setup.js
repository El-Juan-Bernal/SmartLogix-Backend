import '@testing-library/jest-dom'
import { cleanup } from '@testing-library/react'
import { server } from './mocks/server'
import { afterEach } from 'vitest'

afterEach(() => cleanup())

beforeAll(() => server.listen({ onUnhandledRequest: 'warn' }))
afterEach(() => server.resetHandlers())
afterAll(() => server.close())